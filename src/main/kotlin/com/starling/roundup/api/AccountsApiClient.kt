package com.starling.roundup.api

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AccountsApiClient(@Value("\${accessToken}") private val accessToken: String) {
    private val httpClient = OkHttpClient()
    private val baseUrl = "https://api-sandbox.starlingbank.com"

    private val logger: Logger = LoggerFactory.getLogger(AccountsApiClient::class.java)

    fun getAccounts(): List<Account> {
        val url = "$baseUrl/api/v2/accounts"

        val request = Request.Builder()
            .url(url)
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        try {
            val response = httpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()

                // Parse JSON response
                val gson = Gson()
                val accountResponse = gson.fromJson(responseBody, AccountResponse::class.java)

                // Extract list of accounts from the response
                val accounts = accountResponse.accounts ?: emptyList()
                logger.info("Retrieved ${accounts.size} accounts")
                return accounts
            } else {
                logger.error("Failed to retrieve accounts. Response code: ${response.code}")
            }
        } catch (e: Exception) {
            logger.error("An error occurred while retrieving accounts", e)
        }

        return emptyList()
    }
}

data class AccountResponse(val accounts: List<Account>?)

data class Account(
    val accountUid: String,
    val accountType: String,
    val defaultCategory: String,
    val currency: String,
    val createdAt: String,
    val name: String
)