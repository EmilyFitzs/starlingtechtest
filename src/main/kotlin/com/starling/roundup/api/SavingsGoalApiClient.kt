import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import com.google.gson.Gson

class SavingsGoalsApiClient(private val accessToken: String) {
    private val httpClient = OkHttpClient()
    private val baseUrl = "https://api-sandbox.starlingbank.com"

    fun addMoneyToSavingsGoal(accountUid: String, savingsGoalUid: String, transferUid: String, amount: Double) {
        val url = "$baseUrl/api/v2/account/$accountUid/savings-goals/$savingsGoalUid/add-money/$transferUid"

        val requestBody = TopUpRequestV2(
            CurrencyAndAmount("GBP", amount.toInt())
        )
        val jsonRequestBody = Gson().toJson(requestBody)
        val mediaType = "application/json".toMediaType()

        val request = Request.Builder()
            .url(url)
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer $accessToken")
            .put(jsonRequestBody.toRequestBody(mediaType))
            .build()

        httpClient.newCall(request).execute()
    }
}

data class TopUpRequestV2(val amount: CurrencyAndAmount)

data class CurrencyAndAmount(val currency: String, val minorUnits: Int)