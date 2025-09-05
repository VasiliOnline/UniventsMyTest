package com.example.univents.server.models

import kotlinx.serialization.Serializable

@Serializable data class AuthRequest(val email: String, val password: String)
@Serializable data class AuthResponse(val token: String)
@Serializable data class MeDto(val email: String, val displayName: String? = null)

@Serializable
data class EventDto(
    val id: Long,
    val title: String,
    val description: String? = null,
    val date: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)
