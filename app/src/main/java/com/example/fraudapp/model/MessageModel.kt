package com.example.fraudapp.model

import java.io.Serializable

data class MessageModel(
    val id: Int,
    val content: String,
    val isFraud: Boolean,
    val fraudProbability: Float,
    val timestamp: String,
    val sender: String
) : Serializable
