package com.example.ewell

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class Chatbot : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var bottomNavigationView: BottomNavigationView

    private val messages = mutableListOf<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        recyclerView = findViewById(R.id.recycler_view)
        chatAdapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatAdapter
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Set up bottom navigation
        setupBottomNavigation()

        val sendButton = findViewById<ImageButton>(R.id.btn_send)
        val messageInput = findViewById<EditText>(R.id.message)

        sendButton.setOnClickListener {
            val userMessage = messageInput.text.toString().trim()
            if (userMessage.isNotEmpty()) {
                addMessage(userMessage, false)
                botResponse(userMessage)
                messageInput.text.clear()
            }
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homepage -> {
                    // Navigate to home
                    startActivity(Intent(this, HomePage::class.java))
                    true
                }
                R.id.chatbot -> {
                    // Already on chat
                    true
                }
                R.id.profile -> {
                    // Navigate to profile
                    startActivity(Intent(this, Profile::class.java))
                    true
                }
                // Add other navigation items as needed
                else -> false
            }
        }

        // Set the chat item as selected
        bottomNavigationView.selectedItemId = R.id.chatbot
    }

    private fun addMessage(text: String, isBot: Boolean) {
        messages.add(Message(text, isBot))
        chatAdapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)
    }

    private fun botResponse(userMessage: String) {
        val lowercaseMessage = userMessage.lowercase()
        val response = when {
            // Greeting responses
            lowercaseMessage.contains("hi") ||
                    lowercaseMessage.contains("hello") ||
                    lowercaseMessage.contains("hey") -> listOf(
                "Hello there! How are you doing today?",
                "Hi! What can I help you with?",
                "Greetings! I'm ready to assist you."
            ).random()

            // Help-related responses
            lowercaseMessage.contains("help") -> listOf(
                "I'm here to help! What specific assistance do you need?",
                "Sure, I'm ready to support you. What can I do for you?",
                "Help is on the way! Please tell me more about what you're looking for."
            ).random()

            // Wellness-related queries
            lowercaseMessage.contains("health") ||
                    lowercaseMessage.contains("wellness") -> listOf(
                "Wellness is important! What aspect of health would you like to discuss?",
                "Great to hear you're interested in your health. How can I support your wellness journey?",
                "Health and wellness are key to a balanced life. What specific area would you like guidance on?"
            ).random()

            // Emotional support
            lowercaseMessage.contains("feeling") ||
                    lowercaseMessage.contains("mood") -> listOf(
                "I'm here to listen. Would you like to talk about how you're feeling?",
                "Emotions are important. I'm here to provide a supportive ear.",
                "It's okay to not be okay. Would you like to share more about your feelings?"
            ).random()

            // Generic fallback
            else -> listOf(
                "I'm not sure I understand completely. Could you rephrase that?",
                "Interesting! Could you tell me more?",
                "I'm listening. Can you provide more context?",
                "That's an intriguing message. Could you elaborate?"
            ).random()
        }

        // Add a slight delay to make the response feel more natural
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            addMessage(response, true)
        }, 500)
    }
}