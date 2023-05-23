package com.starling.roundup.clients

import com.starling.roundup.clients.model.CurrencyAndAmount
import io.kotest.matchers.nulls.shouldNotBeNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.junit.jupiter.api.Test

@SpringBootTest
@ActiveProfiles("test")
class SavingsGoalsApiClientTest{

    @Autowired
    lateinit var savingsGoalsApiClient: SavingsGoalsApiClient
    @Autowired
    lateinit var accountsApiClient: AccountsApiClient

    @Test
    fun createSavingsGoalReturnsValue() {
        val accounts = accountsApiClient.getAccounts()
        val account = accounts[0]
        val savingsGoal = savingsGoalsApiClient.createSavingsGoal(account.accountUid)
        savingsGoal.shouldNotBeNull()
    }

    @Test
    fun addMoneyToSavingsGoalReturnsValue(){
        val accounts = accountsApiClient.getAccounts()
        val account = accounts[0]
        val savingsGoal = savingsGoalsApiClient.getSavingsGoal(account.accountUid)
        val savingsGoalBefore = savingsGoalsApiClient.getSavingsGoal(account.accountUid, savingsGoalUUID = savingsGoal.savingsGoalUid)
        val topUpRequestV2 = savingsGoalsApiClient.addMoneyToSavingsGoal(account.accountUid, CurrencyAndAmount("GBP", 1020), savingsGoal.savingsGoalUid)
        val savingsGoalAfter = savingsGoalsApiClient.getSavingsGoal(account.accountUid, savingsGoalUUID = savingsGoal.savingsGoalUid)

        assert(savingsGoalAfter.totalSaved!!.minorUnits == savingsGoalBefore.totalSaved!!.minorUnits + 1020)
    }
}