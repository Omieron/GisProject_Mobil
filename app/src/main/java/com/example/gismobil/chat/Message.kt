package com.example.gismobil.chat

data class Message(
    val text: String,
    val timestamp: String,
    val isSent: Boolean
)