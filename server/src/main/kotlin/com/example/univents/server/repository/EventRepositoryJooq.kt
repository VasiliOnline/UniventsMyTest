package com.example.univents.server.repository

import com.example.univents.jooq.tables.records.EventsRecord
import com.example.univents.server.Database.dsl
import com.example.univents.jooq.tables.references.EVENTS
import com.example.univents.server.models.EventDto
import org.jooq.Field
import org.jooq.Record1
import org.jooq.SelectWhereStep
import org.jooq.impl.DSL
import org.jooq.impl.DSL.name
import java.time.LocalDateTime

class EventRepositoryJooq {

    // Поля, которых нет в сгенерированном классе таблицы, берём «по имени»
    // Если у тебя в БД колонки называются иначе (например, lat/lng), поменяй строки ниже.
    private val LAT_FIELD: Field<Double> = DSL.field(name("latitude"), Double::class.java)
    private val LNG_FIELD: Field<Double> = DSL.field(name("longitude"), Double::class.java)

    fun create(
        title: String,
        description: String,
        dateIso: String,
        lat: Double,
        lng: Double,
        creatorEmail: String
    ): Int {
        val dt = LocalDateTime.parse(dateIso)

        val rec: Record1<Long>? = dsl
            .insertInto(EVENTS)
            .set(EVENTS.TITLE, title)
            .set(EVENTS.DESCRIPTION, description)
            .set(EVENTS.DATE, dt)
            .set(LAT_FIELD, lat)
            .set(LNG_FIELD, lng)
            .set(EVENTS.CREATOR_EMAIL, creatorEmail)
            .returningResult(EVENTS.ID) // ID у нас BIGINT -> Long
            .fetchOne() as Record1<Long>?

        val idLong = rec?.value1() ?: error("Insert failed")
        return idLong.toInt() // если DTO использует Int
    }

    fun getById(id: Int): EventDto? =
        dsl.selectFrom(EVENTS)
            .where(EVENTS.ID.eq(id.toLong()))
            .fetchOne()
            ?.let { row ->
                EventDto(
                    id = (row.id ?: 0L).toInt(),
                    title = row.title ?: "",
                    description = row.description ?: "",
                    date = row.date?.toString() ?: "",
                    latitude = row.get(LAT_FIELD) ?: 0.0,
                    longitude = row.get(LNG_FIELD) ?: 0.0,
                    creatorEmail = row.creatorEmail ?: ""
                )
            }

    fun update(
        id: Int,
        requesterEmail: String,
        title: String,
        description: String,
        dateIso: String,
        lat: Double,
        lng: Double
    ): Boolean =
        dsl.update(EVENTS)
            .set(EVENTS.TITLE, title)
            .set(EVENTS.DESCRIPTION, description)
            .set(EVENTS.DATE, LocalDateTime.parse(dateIso))
            .set(LAT_FIELD, lat)
            .set(LNG_FIELD, lng)
            .where(
                EVENTS.ID.eq(id.toLong())
                    .and(EVENTS.CREATOR_EMAIL.eq(requesterEmail))
            )
            .execute() > 0

    fun delete(id: Int, requesterEmail: String): Boolean =
        dsl.deleteFrom(EVENTS)
            .where(
                EVENTS.ID.eq(id.toLong())
                    .and(EVENTS.CREATOR_EMAIL.eq(requesterEmail))
            )
            .execute() > 0

    /**
     * Простейшая фильтрация: либо bbox (если все 4 заданы), либо радиус (км) от точки,
     * иначе — просто последние события.
     */
    fun list(
        north: Double? = null, south: Double? = null,
        east: Double? = null, west: Double? = null,
        centerLat: Double? = null, centerLng: Double? = null, radiusKm: Double? = null,
        limit: Int = 200, offset: Int = 0
    ): List<EventDto> {
        var query = dsl.selectFrom(EVENTS)

        if (listOf(north, south, east, west).all { it != null }) {
            query = query.where(
                LAT_FIELD.le(north!!)
                    .and(LAT_FIELD.ge(south!!))
                    .and(LNG_FIELD.le(east!!))
                    .and(LNG_FIELD.ge(west!!))
            ) as SelectWhereStep<EventsRecord?>
        } else if (centerLat != null && centerLng != null && radiusKm != null) {
            // Грубая аппроксимация радиуса через градусы (1° ~ 111 км)
            val deg = radiusKm / 111.0
            query = query.where(
                LAT_FIELD.between(centerLat - deg, centerLat + deg)
                    .and(LNG_FIELD.between(centerLng - deg, centerLng + deg))
            ) as SelectWhereStep<EventsRecord?>
        }

        return query
            .orderBy(EVENTS.DATE.desc())
            .limit(limit)
            .offset(offset)
            .fetch()
            .map { r ->
                EventDto(
                    id = (r.id ?: 0L).toInt(),
                    title = r.title ?: "",
                    description = r.description ?: "",
                    date = r.date?.toString() ?: "",
                    latitude = r.get(LAT_FIELD) ?: 0.0,
                    longitude = r.get(LNG_FIELD) ?: 0.0,
                    creatorEmail = r.creatorEmail ?: ""
                )
            }
    }
}
