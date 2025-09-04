package com.example.univents.server.models

import org.jetbrains.exposed.dao.id.IntIdTable

object Events : IntIdTable() {
    val title = varchar("title", 255)
    val description = text("description")
    val date = varchar("date", 50)  // для простоты, можно позже LocalDateTime
    val creatorEmail = varchar("creator_email", 255) // связываем с пользователем
}

