package com.example.univents.server.models

import kotlinx.serialization.Serializable

@Serializable
data class UpdateEventRequest(
    val id: Int,
    val title: String,
    val description: String,
    val date: String
)

@Serializable
data class DeleteEventRequest(
    val id: Int
)
