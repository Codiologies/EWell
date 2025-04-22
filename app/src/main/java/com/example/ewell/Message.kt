package com.example.ewell

data class Message(
    val text: String,
    val isBot: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
