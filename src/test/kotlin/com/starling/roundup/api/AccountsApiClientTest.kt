package com.starling.roundup.api

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer

class AccountsApiClientTest : FunSpec() {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var accountsApiClient: AccountsApiClient

    init {
        beforeTest {
            mockWebServer = MockWebServer()
            mockWebServer.start()

            val baseUrl = mockWebServer.url("/").toString()
            val accessToken = "test-access-token"

            accountsApiClient = AccountsApiClient(accessToken)
            accountsApiClient.baseUrl = baseUrl
        }

        afterTest {
            mockWebServer.shutdown()
        }

        test("getAccounts should return list of accounts") {
            val responseBody = """
                {
                  "accounts": [
                    {
                      "accountUid": "6e7eac33-3bc2-4e3d-9ddf-2ebf5e91b59e",
                      "accountType": "PRIMARY",
                      "defaultCategory": "6e7e4af5-6731-47bc-a8ef-01f941fcf506",
                      "currency": "GBP",
                      "createdAt": "2023-05-21T09:34:07.087Z",
                      "name": "Joint"
                    }
                  ]
                }
            """.trimIndent()

            val mockResponse = MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)

            mockWebServer.enqueue(mockResponse)

            val accounts = accountsApiClient.getAccounts()

            val expectedAccounts = listOf(
                Account(
                    "6e7eac33-3bc2-4e3d-9ddf-2ebf5e91b59e",
                    "PRIMARY",
                    "6e7e4af5-6731-47bc-a8ef-01f941fcf506",
                    "GBP",
                    "2023-05-21T09:34:07.087Z",
                    "Joint"
                )
            )

            accounts shouldBe expectedAccounts
        }
    }
}
