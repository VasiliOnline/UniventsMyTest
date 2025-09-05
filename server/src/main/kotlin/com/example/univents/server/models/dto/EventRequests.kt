package com.example.univents.server.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateEventRequest(
    val title: String,
    val description: String,
    val date: String, // ISO-8601
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class UpdateEventRequest(
    val title: String,
    val description: String,
    val date: String, // ISO-8601
    val latitude: Double,
    val longitude: Double
)
