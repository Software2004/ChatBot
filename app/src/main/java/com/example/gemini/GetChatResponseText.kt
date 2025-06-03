package com.example.gemini

import android.util.Log
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GetChatResponseText {

    private lateinit var chat: Chat
    private var stringBuilder: StringBuilder = StringBuilder()

    interface ResponseCallback {
        fun onSuccess(response: String)
        fun onError(errorMessage: String)
    }

    fun getResponse(text: String, callback: ResponseCallback) {
        val generativeModel = GenerativeModel(modelName = "gemini-2.0-flash", apiKey = "AIzaSyCIsJvP69PQGQwG7NQqVLdEMBOlm5ToqRc")
        chat = generativeModel.startChat(
            history = listOf()
        )

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    chat.sendMessage(text)
                }
                stringBuilder.append(result.text + "\n\n")
                callback.onSuccess(stringBuilder.toString())
            } catch (e: Exception) {
                Log.e("TAG", "Error during chat: ${e.message}")
                callback.onError("An error occurred. Please try again.")
            }
        }
    }
}
