package com.example.fraudapp.network

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object ApiClient {

    // ── UPDATED WITH YOUR REAL LAPTOP IP FROM IPCONFIG ────────────────────────
    // This is your Wi-Fi IPv4 Address: 10.119.86.129
    // Phone and laptop MUST be on same Wi-Fi network
    // If using emulator instead of real phone, change to "http://10.0.2.2:8000"
    private const val BASE_URL = "http://10.119.86.129:8000"
    // ─────────────────────────────────────────────────────────────

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    data class PredictionResult(
        val result: String,
        val status: String,
        val isFraud: Boolean,
        val fraudProbability: Float
    )

    fun predict(message: String): PredictionResult {
        // Build JSON using Android's built-in JSONObject (no extra dependency needed)
        val requestJson = JSONObject()
        requestJson.put("message", message)

        val body = requestJson.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url("$BASE_URL/predict")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            throw Exception("Server error: ${response.code} ${response.message}")
        }

        val responseBody = response.body?.string()
            ?: throw Exception("Empty response body from server")

        val json = JSONObject(responseBody)
        val result = json.getString("result")
        val status = json.getString("status")
        val isFraud = result.lowercase() == "fraud"

        val fraudProbability: Float = when {
            json.has("probability") -> (json.getDouble("probability") * 100f).toFloat()
            isFraud -> 92f
            else -> 4f
        }

        return PredictionResult(
            result = result,
            status = status,
            isFraud = isFraud,
            fraudProbability = fraudProbability.coerceIn(0f, 100f)
        )
    }
}