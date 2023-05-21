package com.starling.roundup.api

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
class AccountsApiClient(private val accessToken: String) {
    private val httpClient = OkHttpClient()
    var baseUrl = "https://api-sandbox.starlingbank.com"

    fun getAccounts(): List<Account> {
        val url = "$baseUrl/api/v2/accounts"

        val request = Request.Builder()
            .url(url)
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        val response = httpClient.newCall(request).execute()

        val responseBody = response.body?.string()

        // Parse the JSON response
        val gson = Gson()
        val accountResponse = gson.fromJson(responseBody, AccountResponse::class.java)

        // Extract the list of accounts from the response
        return accountResponse.accounts ?: emptyList()
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