package com.starling.roundup.clients

import com.starling.roundup.clients.model.FeedItem
import com.google.gson.Gson
import com.starling.roundup.service.RoundUpProcessor
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
class RoundUpProcessorTest {

    @Autowired
    lateinit var accountsApiClient: AccountsApiClient

    @Autowired
    lateinit var savingsGoalsApiClient: SavingsGoalsApiClient

    @Test
    fun processRoundUpForWeek() {
        val accounts = accountsApiClient.getAccounts()
        val account = accounts[0]
        val accountUid = account.accountUid
        val categoryUid = account.defaultCategory
        val startDate: ZonedDateTime =  ZonedDateTime.of(2022, 5, 14, 0, 0, 0, 0, ZoneId.of("UTC"))
        val endDate: ZonedDateTime = ZonedDateTime.of(2023, 5, 7, 0, 0, 0, 0, ZoneId.of("UTC"))
        val savingsGoalStart = savingsGoalsApiClient.getSavingsGoal(account.accountUid)

        val testJson = """
            [
                {
                    "feedItemUid": "75bdb29d-9866-49bc-ab98-5b6fc7bdc903",
                    "categoryUid": "6e7e4af5-6731-47bc-a8ef-01f941fcf506",
                    "amount": {
                        "currency": "GBP",
                        "minorUnits": 338
                    },
                    "sourceAmount": {
                        "currency": "GBP",
                        "minorUnits": 338
                    },
                    "direction": "OUT",
                    "updatedAt": "2023-05-22T16:29:43.309Z",
                    "transactionTime": "2023-05-22T16:29:42.000Z",
                    "settlementTime": "2023-05-22T16:29:43.243Z",
                    "source": "MASTER_CARD",
                    "sourceSubType": "CHIP_AND_PIN",
                    "status": "SETTLED",
                    "transactingApplicationUserUid": "6e7d6c34-0465-4c6c-bd69-3ecdb721aa1e",
                    "counterPartyType": "MERCHANT",
                    "counterPartyUid": "66ee126e-be32-4fd6-a779-218ee9275bfe",
                    "counterPartyName": "Marks&spencer",
                    "counterPartySubEntityUid": "a0c32d1d-0685-4f9e-a288-05169caac5b1",
                    "reference": "MARKS&SPENCER PLC SACA",
                    "country": "GB",
                    "spendingCategory": "EATING_OUT",
                    "hasAttachment": false,
                    "hasReceipt": false,
                    "batchPaymentDetails": null
                },
                {
                    "feedItemUid": "75bd341b-bdfb-47f5-a443-59ceecc622dd",
                    "categoryUid": "6e7e4af5-6731-47bc-a8ef-01f941fcf506",
                    "amount": {
                        "currency": "GBP",
                        "minorUnits": 673
                    },
                    "sourceAmount": {
                        "currency": "GBP",
                        "minorUnits": 673
                    },
                    "direction": "OUT",
                    "updatedAt": "2023-05-22T16:29:42.506Z",
                    "transactionTime": "2023-05-22T16:29:40.000Z",
                    "settlementTime": "2023-05-22T16:29:42.050Z",
                    "source": "MASTER_CARD",
                    "sourceSubType": "CHIP_AND_PIN",
                    "status": "SETTLED",
                    "transactingApplicationUserUid": "6e7d6c34-0465-4c6c-bd69-3ecdb721aa1e",
                    "counterPartyType": "MERCHANT",
                    "counterPartyUid": "66ee126e-be32-4fd6-a779-218ee9275bfe",
                    "counterPartyName": "Marks&spencer",
                    "counterPartySubEntityUid": "a0c32d1d-0685-4f9e-a288-05169caac5b1",
                    "reference": "MARKS&SPENCER PLC SACA",
                    "country": "GB",
                    "spendingCategory": "EATING_OUT",
                    "hasAttachment": false,
                    "hasReceipt": false,
                    "batchPaymentDetails": null
                },
                {
                    "feedItemUid": "75bd2961-4a56-457a-aada-c11a8d5183a4",
                    "categoryUid": "6e7e4af5-6731-47bc-a8ef-01f941fcf506",
                    "amount": {
                        "currency": "GBP",
                        "minorUnits": 501
                    },
                    "sourceAmount": {
                        "currency": "GBP",
                        "minorUnits": 501
                    },
                    "direction": "OUT",
                    "updatedAt": "2023-05-22T16:29:27.895Z",
                    "transactionTime": "2023-05-22T16:29:26.357Z",
                    "settlementTime": "2023-05-22T16:29:27.822Z",
                    "source": "FASTER_PAYMENTS_OUT",
                    "status": "SETTLED",
                    "transactingApplicationUserUid": "6e7d6c34-0465-4c6c-bd69-3ecdb721aa1e",
                    "counterPartyType": "PAYEE",
                    "counterPartyUid": "75bd0e15-2501-4e87-a871-248d2f9cf488",
                    "counterPartyName": "Mickey Mouse",
                    "counterPartySubEntityUid": "75bd1e3c-0124-417a-9062-8214a10307be",
                    "counterPartySubEntityName": "UK account",
                    "counterPartySubEntityIdentifier": "204514",
                    "counterPartySubEntitySubIdentifier": "00000825",
                    "reference": "External Payment",
                    "country": "GB",
                    "spendingCategory": "PAYMENTS",
                    "hasAttachment": false,
                    "hasReceipt": false,
                    "batchPaymentDetails": null
                }
            ]
        """.trimIndent()
        val feedItems = Gson().fromJson(testJson, Array<FeedItem>::class.java).toList()

        val transactionFeedApiClient = Mockito.mock(TransactionFeedApiClientInterface::class.java)
        Mockito.`when`( transactionFeedApiClient.getTransactions(accountUid, categoryUid, null, null) ).thenReturn(feedItems)

        val roundUpProcessor = RoundUpProcessor(transactionFeedApiClient, savingsGoalsApiClient)
        roundUpProcessor.processRoundUpForWeek(accountUid, categoryUid)

        val expectedRoundUpAmount = 0.89

        // Get the savings goal after the round-up process
        val savingsGoal = savingsGoalsApiClient.getSavingsGoal(account.accountUid)
        savingsGoal.savedPercentage.shouldNotBeNull()
        savingsGoal.shouldNotBeNull()

        // Assert that the totalSaved amount in the savings goal has increased by the expected round-up amount
        assert(expectedRoundUpAmount == (savingsGoal.totalSaved!!.minorUnits - savingsGoalStart.totalSaved!!.minorUnits)/ 100.0)
    }
}

