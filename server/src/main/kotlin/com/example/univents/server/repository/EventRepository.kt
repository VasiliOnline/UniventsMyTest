package com.example.univents.server.repository

import com.example.univents.server.models.EventDto
import com.example.univents.server.models.Events
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll

class EventRepository {

    fun createEvent(
        title: String,
        description: String,
        dateIso: String,
        latitude: Double,
        longitude: Double,
        creatorEmail: String
    ): Int = transaction {
        val dt = LocalDateTime.parse(dateIso)
        Events.insertAndGetId { row ->
            row[Events.title] = title
            row[Events.description] = description
            row[Events.date] = dt
            row[Events.latitude] = latitude
            row[Events.longitude] = longitude
            row[Events.creatorEmail] = creatorEmail
        }.value
    }

    fun getById(id: Int): EventDto? = transaction {
        Events.select { Events.id eq id }.singleOrNull()?.let { it.toDto() }
    }

    fun updateEvent(
        id: Int,
        requesterEmail: String,
        title: String,
        description: String,
        dateIso: String,
        latitude: Double,
        longitude: Double
    ): Boolean = transaction {
        val dt = LocalDateTime.parse(dateIso)
        val updated = Events.update({ (Events.id eq id) and (Events.creatorEmail eq requesterEmail) }) { row ->
            row[Events.title] = title
            row[Events.description] = description
            row[Events.date] = dt
            row[Events.latitude] = latitude
            row[Events.longitude] = longitude
        }
        updated > 0
    }

    fun deleteEvent(id: Int, requesterEmail: String): Boolean = transaction {
        Events.deleteWhere { (Events.id eq id) and (Events.creatorEmail eq requesterEmail) } > 0
    }

    fun getAllEvents(
        north: Double? = null,
        south: Double? = null,
        east: Double? = null,
        west: Double? = null,
        limit: Int = 200,
        offset: Int = 0
    ): List<EventDto> = transaction {
        val base = Events.selectAll()
        val filtered = if (listOf(north, south, east, west).all { it != null }) {
            base.andWhere { Events.latitude lessEq (north!!) }
                .andWhere { Events.latitude greaterEq (south!!) }
                .andWhere { Events.longitude lessEq (east!!) }
                .andWhere { Events.longitude greaterEq (west!!) }
        } else base

        filtered
            .orderBy(Events.date to SortOrder.DESC)
            .limit(limit, offset.toLong())
            .map { it.toDto() }
    }
}

private fun ResultRow.toDto(): EventDto = EventDto(
    id = this[Events.id].value,
    title = this[Events.title],
    description = this[Events.description],
    date = this[Events.date].toString(),
    latitude = this[Events.latitude],
    longitude = this[Events.longitude],
    creatorEmail = this[Events.creatorEmail]
)
