package com.example.gemini

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GeminiApiService {
    @Headers("Content-Type: application/json")
    @POST("/chatbot-endpoint") // Replace with the actual Gemini API endpoint
    fun sendMessage(@Body request: GeminiRequest): Call<GeminiResponse>
}

data class GeminiRequest(val message: String)
data class GeminiResponse(val response: String)
