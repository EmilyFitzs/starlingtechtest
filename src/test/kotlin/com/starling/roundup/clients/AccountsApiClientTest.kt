package com.starling.roundup.clients

import io.kotest.matchers.collections.shouldNotBeEmpty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

import org.junit.jupiter.api.Test

@SpringBootTest
@ActiveProfiles("test")
class AccountsApiClientTest {

    @Autowired
    lateinit var accountsApiClient: AccountsApiClient
    @Test
    fun getAccountsReturnExpectedValues() {
        val accounts = accountsApiClient.getAccounts()

        accounts.shouldNotBeEmpty()
        assert(accounts[0].name == "Joint")
        assert(accounts[0].accountUid == "6e7eac33-3bc2-4e3d-9ddf-2ebf5e91b59e")
        assert(accounts[0].currency == "GBP")
    }
}
