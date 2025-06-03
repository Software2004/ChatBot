package com.example.gemini
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.gemini.com/" // Replace with Gemini API base URL

    private val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: GeminiApiService by lazy {
        instance.create(GeminiApiService::class.java)
    }
}

