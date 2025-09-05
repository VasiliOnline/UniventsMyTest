package com.example.univents.server.models

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object Events : IntIdTable() {
    val title = varchar("title", 255)
    val description = text("description")
    val date = datetime("date")
    val creatorEmail = varchar("creator_email", 255).index()
    val latitude = double("latitude")
    val longitude = double("longitude")
}
