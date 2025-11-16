package com.example.turnofacil

data class Turn(
    val turnNumber: String,
    val status: String,
    val isAttending: Boolean = false
)
