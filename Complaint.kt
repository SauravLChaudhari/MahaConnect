package com.example.mahaconnect

data class Complaint(
    val id: Int = 0,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "pending"
)