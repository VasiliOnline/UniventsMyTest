package com.example.univents.server.models

data class AuthResponse(
    val token: String?,
    val message: String,
    val email: String?
)
