package com.example.univents.server.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class EventDto(
    val id: Int,
    val title: String,
    val description: String,
    /** ISO-8601, e.g. 2025-09-04T10:30:00 */
    val date: String,
    val latitude: Double,
    val longitude: Double,
    val creatorEmail: String
)
