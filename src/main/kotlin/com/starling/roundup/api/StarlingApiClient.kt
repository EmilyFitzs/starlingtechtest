package com.starling.roundup.api

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request

class StarlingApiClient(private val accessToken: String) {
    private val httpClient = OkHttpClient()
    private val baseUrl = "https://api-sandbox.starlingbank.com"

    fun getTransactions(customerId: String): String {
        val url = baseUrl.toHttpUrlOrNull()?.newBuilder()
            ?.addPathSegment("api")
            ?.addPathSegment("v2")
            ?.addPathSegment("customers")
            ?.addPathSegment(customerId)
            ?.addPathSegment("transactions")
            ?.build()

        val request = url?.let {
            Request.Builder()
                .url(it)
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
        }

        val response = request?.let { httpClient.newCall(it).execute() }

        return response!!.body?.string() ?: ""
    }
}