package com.starling.roundup.clients

import java.util.*
import com.starling.roundup.clients.model.CreateOrUpdateSavingsGoalResponseV2
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import com.google.gson.Gson
import com.starling.roundup.clients.model.CurrencyAndAmount
import com.starling.roundup.clients.model.SavingsGoalV2
import com.starling.roundup.clients.model.TopUpRequestV2
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class SavingsGoalsApiClient(
    @Value("\${accessToken}") private val accessToken: String,
    @Value("\${savingsGoalName}")private val savingsGoalName: String,
    @Value("\${savingsGoalTarget}")private val savingsGoalTarget: Int,
    @Value("\${currency}") private val currency: String,
    @Value("\${savingsGoalUuid}") private val defaultGoalUuid: String
) {
    private val httpClient = OkHttpClient()
    private val baseUrl = "https://api-sandbox.starlingbank.com"

    fun createSavingsGoal(accountUid: String): CreateOrUpdateSavingsGoalResponseV2 {
        val url = "$baseUrl/api/v2/account/$accountUid/savings-goals"

        val requestBody = SavingsGoalRequest(savingsGoalName, currency, CurrencyAndAmount(currency, savingsGoalTarget))
        val jsonRequestBody = Gson().toJson(requestBody)
        val mediaType = "application/json".toMediaType()

        val request = Request.Builder()
            .url(url)
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer $accessToken")
            .put(jsonRequestBody.toRequestBody(mediaType))
            .build()

        val response = httpClient.newCall(request).execute()
        val responseBody = response.body?.string()

        // Parse the JSON response
        val gson = Gson()
        val savingsGoalResponse = gson.fromJson(responseBody, CreateOrUpdateSavingsGoalResponseV2::class.java)

        // Extract the savings goal UID from the response
        return savingsGoalResponse ?: throw IllegalStateException("Failed to create savings goal")
    }

    fun addMoneyToSavingsGoal(accountUid: String, amount: CurrencyAndAmount, savingsGoalUid: String? = null, transferUid: String? = null): TopUpRequestV2 {
        val savingsGoal = savingsGoalUid ?: defaultGoalUuid
        val transferUidCreated = transferUid ?: UUID.randomUUID().toString()
        val url = "$baseUrl/api/v2/account/$accountUid/savings-goals/${savingsGoal}/add-money/$transferUidCreated"

        val requestBody = TopUpRequestV2(amount)
        val jsonRequestBody = Gson().toJson(requestBody)
        val mediaType = "application/json".toMediaType()

        val request = Request.Builder()
            .url(url)
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer $accessToken")
            .put(jsonRequestBody.toRequestBody(mediaType))
            .build()

        val response = httpClient.newCall(request).execute()
        val responseBody = response.body?.string()

        if (response.isSuccessful && responseBody != null) {
            val gson = Gson()
            val savingsGoalResponse = gson.fromJson(responseBody, TopUpRequestV2::class.java)

            return savingsGoalResponse ?: throw IllegalStateException("Failed to parse response")
        } else {
            throw IllegalStateException("Failed to add to savings goal: ${response.code} ${response.message}")
        }
    }

    fun getSavingsGoal(accountUid: String, savingsGoalUUID: String? = null): SavingsGoalV2 {
        val savingsGoalUidParam: String = savingsGoalUUID ?: defaultGoalUuid
        val url = "$baseUrl/api/v2/account/$accountUid/savings-goals/${savingsGoalUidParam}/"
        val request = Request.Builder()
            .url(url)
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer $accessToken")
            .get()
            .build()
        val response = httpClient.newCall(request).execute()
        val responseBody = response.body?.string()

        val gson = Gson()
        val savingsGoalResponse = gson.fromJson(responseBody, SavingsGoalV2::class.java)

        return savingsGoalResponse ?: throw IllegalStateException("Failed to get savings goal")
    }
}

data class SavingsGoalRequest(val name: String, val currency: String, val target: CurrencyAndAmount)