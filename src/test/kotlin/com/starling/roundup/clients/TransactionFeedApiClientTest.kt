package com.starling.roundup.clients

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

import org.junit.jupiter.api.Test

@SpringBootTest
@ActiveProfiles("test")
class TransactionFeedApiClientTest{

    @Autowired
    lateinit var transactionFeedApiClient: TransactionFeedApiClient
    @Autowired
    lateinit var accountsApiClient: AccountsApiClient
    @Test
    fun getTransactionsReturnsExpectedValue() {
        val accounts = accountsApiClient.getAccounts()
        val account = accounts[0]
        val transactions = transactionFeedApiClient.getTransactions(account.accountUid, account.defaultCategory)

        assert(transactions.isNotEmpty())
    }
}
