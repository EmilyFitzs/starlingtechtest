package com.starling.roundup.clients

import java.util.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import com.google.gson.Gson
import com.starling.roundup.clients.model.CurrencyAndAmount
import com.starling.roundup.clients.model.SavingsGoalV2
import com.starling.roundup.clients.model.TopUpRequestV2
import okhttp3.Response
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class SavingsGoalsApiClient(
    @Value("\${accessToken}") private val accessToken: String,
    @Value("\${savingsGoalName}") private val savingsGoalName: String,
    @Value("\${savingsGoalTarget}") private val savingsGoalTarget: Int,
    @Value("\${currency}") private val currency: String,
    @Value("\${savingsGoalUuid}") private val defaultGoalUuid: String
) {
    private val httpClient = OkHttpClient()
    private val baseUrl = "https://api-sandbox.starlingbank.com"

    private fun createRequestBuilder(url: String): Request.Builder {
        return Request.Builder()
            .url(url)
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer $accessToken")
    }

    private fun executeRequest(request: Request): Response {
        val response = httpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IllegalStateException("Request failed: ${response.code} ${response.message}")
        }
        return response
    }

    private fun <T> parseJsonResponse(responseBody: String?, clazz: Class<T>): T {
        val gson = Gson()
        return gson.fromJson(responseBody, clazz) ?: throw IllegalStateException("Failed to parse response")
    }

    fun addMoneyToSavingsGoal(accountUid: String, amount: CurrencyAndAmount, savingsGoalUid: String? = null, transferUid: String? = null): TopUpRequestV2 {
        val savingsGoal = savingsGoalUid ?: defaultGoalUuid
        val transferUidCreated = transferUid ?: UUID.randomUUID().toString()
        val url = "$baseUrl/api/v2/account/$accountUid/savings-goals/${savingsGoal}/add-money/$transferUidCreated"

        val requestBody = TopUpRequestV2(amount)
        val jsonRequestBody = Gson().toJson(requestBody)
        val mediaType = "application/json".toMediaType()

        val request = createRequestBuilder(url)
            .put(jsonRequestBody.toRequestBody(mediaType))
            .build()

        val response = executeRequest(request)
        val responseBody = response.body?.string()

        return parseJsonResponse(responseBody, TopUpRequestV2::class.java)
    }

    fun getSavingsGoal(accountUid: String, savingsGoalUUID: String? = null): SavingsGoalV2 {
        val savingsGoalUidParam: String = savingsGoalUUID ?: defaultGoalUuid
        val url = "$baseUrl/api/v2/account/$accountUid/savings-goals/${savingsGoalUidParam}/"
        val request = createRequestBuilder(url)
            .get()
            .build()

        val response = executeRequest(request)
        val responseBody = response.body?.string()

        return parseJsonResponse(responseBody, SavingsGoalV2::class.java)
    }
}

data class SavingsGoalRequest(val name: String, val currency: String, val target: CurrencyAndAmount)