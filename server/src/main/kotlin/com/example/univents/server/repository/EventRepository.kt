package com.example.univents.server.repository

import com.example.univents.server.models.Events
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

data class EventDTO(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    val creatorEmail: String
)

class EventRepository {

    fun createEvent(title: String, description: String, date: String, creatorEmail: String): Int {
        return transaction {
            Events.insert {
                it[Events.title] = title
                it[Events.description] = description
                it[Events.date] = date
                it[Events.creatorEmail] = creatorEmail
            } get Events.id
        }.value
    }

    fun getAllEvents(): List<EventDTO> {
        return transaction {
            Events.selectAll().map {
                EventDTO(
                    id = it[Events.id].value,
                    title = it[Events.title],
                    description = it[Events.description],
                    date = it[Events.date],
                    creatorEmail = it[Events.creatorEmail]
                )
            }
        }
    }

    fun getEventsByUser(email: String): List<EventDTO> {
        return transaction {
            Events.select { Events.creatorEmail eq email }.map {
                EventDTO(
                    id = it[Events.id].value,
                    title = it[Events.title],
                    description = it[Events.description],
                    date = it[Events.date],
                    creatorEmail = it[Events.creatorEmail]
                )
            }
        }
    }
    // Редактирование события (только создатель)
    fun updateEvent(eventId: Int, email: String, title: String, description: String, date: String): Boolean {
        return transaction {
            val updatedRows = Events.update({ Events.id eq eventId and (Events.creatorEmail eq email) }) {
                it[Events.title] = title
                it[Events.description] = description
                it[Events.date] = date
            }
            updatedRows > 0
        }
    }

    // Удаление события (только создатель)
    fun deleteEvent(eventId: Int, email: String): Boolean {
        return transaction {
            val deletedRows = Events.deleteWhere { Events.id eq eventId and (Events.creatorEmail eq email) }
            deletedRows > 0
        }
    }
}
