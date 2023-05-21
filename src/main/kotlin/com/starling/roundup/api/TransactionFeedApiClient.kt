package com.starling.roundup.api

import com.google.gson.Gson
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class TransactionFeedApiClient(private val accessToken: String) {
    private val httpClient = OkHttpClient()
    private val baseUrl = "https://api-sandbox.starlingbank.com"

    fun getTransactions(accountUid: String, categoryUid: String): List<Transaction> {
        val currentDate = LocalDate.now()
        val startDate = currentDate.minusWeeks(1)
        val endDate = currentDate

        val urlBuilder = "$baseUrl/api/v2/feed/account/$accountUid/category/$categoryUid/transactions-between".toHttpUrlOrNull()
            ?.newBuilder()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        urlBuilder?.addQueryParameter("minTransactionTimestamp", startDate.atStartOfDay().toInstant(ZoneOffset.UTC).toString())
        urlBuilder?.addQueryParameter("maxTransactionTimestamp", endDate.atStartOfDay().toInstant(ZoneOffset.UTC).toString())

        val url = urlBuilder?.build().toString()

        val request = Request.Builder()
            .url(url)
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        val response = httpClient.newCall(request).execute()

        val responseBody = response.body?.string()

        // Parse the JSON response
        val gson = Gson()
        val transactionResponse = gson.fromJson(responseBody, TransactionResponse::class.java)

        // Extract the list of transactions from the response
        return transactionResponse.transactions ?: emptyList()
    }
}

data class TransactionResponse(val transactions: List<Transaction>?)

data class Transaction(
    val transactionId: String,
    val amount: Double,
    val date: String,
    val category: String
)