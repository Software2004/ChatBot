package com.example.gemini

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var editTextInput: EditText
    private lateinit var responseText: TextView
    private lateinit var btnSend: Button
    private lateinit var btnNewChat: FloatingActionButton
    private lateinit var progressBar: ProgressBar  // ProgressBar
//    private val recyclerViewChat: RecyclerView? = null
//    private val chatAdapter: ChatAdapter? = null
//    private val chatMessageList: List<ChatMessage>? = null

    private lateinit var chat: Chat
    private var stringBuilder: StringBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextInput = findViewById(R.id.userInput)
        responseText = findViewById(R.id.chatbotResponse)
        btnSend = findViewById(R.id.sendButton)
        btnNewChat = findViewById(R.id.chat)
        progressBar = findViewById(R.id.progressBar)  // Initialize ProgressBar

        val generativeModel = GenerativeModel(modelName = "gemini-pro",apiKey = "AIzaSyCIsJvP69PQGQwG7NQqVLdEMBOlm5ToqRc")
        chat = generativeModel.startChat(
            history = listOf(
                content(role = "user") { text("Hello, I have 2 dogs in my house.") },
                content(role = "model") { text("Great to meet you. What would you like to know?") }
            )
        )
//        stringBuilder.append("Hello, I have 2 dogs in my house.\n\n")
//        stringBuilder.append("Great to meet you. What would you like to know?\n\n")

        btnSend.setOnClickListener {
            Log.e("TAG", "onCreate: Send btn Clicked")
            buttonSendChat()
        }
        btnNewChat.setOnClickListener {
            val intent = Intent(this, UsingDistilGPT2::class.java) // Replace SecondActivity with your target activity
            startActivity(intent)
        }


    }

    private fun buttonSendChat() {
        stringBuilder.append(editTextInput.text.toString() + "\n\n")
        responseText.text = stringBuilder.toString()

        // Show the progress bar when send button is clicked
        progressBar.visibility = View.VISIBLE
        btnSend.isEnabled = false // Disable button to prevent multiple requests

        MainScope().launch {
            try {
                val result = chat.sendMessage(editTextInput.text.toString())
                stringBuilder.append(result.text + "\n\n")
                responseText.text = stringBuilder.toString()
            } catch (e: Exception) {
                Log.e("TAG", "Error during chat: ${e.message}")
                responseText.text = "An error occurred. Please try again."
            } finally {
                // Hide progress bar and enable the button again after the response is received
                progressBar.visibility = View.GONE
                btnSend.isEnabled = true
                editTextInput.setText("")
            }
        }
    }
}
