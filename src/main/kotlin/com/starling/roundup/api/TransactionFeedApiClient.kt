package com.starling.roundup.api

import com.starling.roundup.api.model.FeedItem
import com.google.gson.Gson
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

interface TransactionFeedApiClientInterface {
    fun getTransactions(
        accountUid: String,
        categoryUid: String,
        startDate: ZonedDateTime? = null,
        endDate: ZonedDateTime? = null
    ): List<FeedItem>
}

@Component
class TransactionFeedApiClient(@Value("\${accessToken}") private val accessToken: String): TransactionFeedApiClientInterface {
    private val httpClient = OkHttpClient()
    private val baseUrl = "https://api-sandbox.starlingbank.com"

    override fun getTransactions(accountUid: String, categoryUid: String, startDate: ZonedDateTime?, endDate: ZonedDateTime?): List<FeedItem> {
        val endDateAdjusted: ZonedDateTime = endDate ?: ZonedDateTime.now()
        val startDateAdjusted: ZonedDateTime = startDate ?: endDateAdjusted.minusDays(7)
        val urlBuilder = "$baseUrl/api/v2/feed/account/$accountUid/category/$categoryUid/transactions-between".toHttpUrlOrNull()
            ?.newBuilder()

        urlBuilder?.addQueryParameter("minTransactionTimestamp", startDateAdjusted.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
        urlBuilder?.addQueryParameter("maxTransactionTimestamp", endDateAdjusted.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))

        val url = urlBuilder?.build().toString()

        val request = Request.Builder()
            .url(url)
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer $accessToken")
            .get()
            .build()

        val response = httpClient.newCall(request).execute()

        val responseBody = response.body?.string()

        // Parse the JSON response
        val gson = Gson()
        val transactionResponse = gson.fromJson(responseBody, TransactionResponse::class.java)

        // Extract the list of transactions from the response
        return transactionResponse.feedItems ?: emptyList()
    }
}

data class TransactionResponse(val feedItems: List<FeedItem>?)
