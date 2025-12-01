package com.example.turnofacil

data class Turn(
    val id: Int = 0,
    val turnNumber: String,
    val status: String,
    val isAttending: Boolean = false,
    val createdAt: String = ""
)
