package com.example.univents.server.repository

import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import org.jooq.Field
import java.time.OffsetDateTime

class EventRepositoryJooq(private val dsl: DSLContext) {

    fun listAll(limit: Int = 100, offset: Int = 0): List<Map<String, Any?>> =
        dsl.select(
            field("id"),
            field("title"),
            field("description"),
            field("starts_at"),
            field("latitude"),
            field("longitude")
        ).from("events")
         .orderBy(field("starts_at").desc())
         .limit(limit)
         .offset(offset)
         .fetch { it.intoMap() }

    fun listByBbox(west: Double, south: Double, east: Double, north: Double, limit: Int = 200, offset: Int = 0): List<Map<String, Any?>> =
        dsl.select(
            field("id"),
            field("title"),
            field("description"),
            field("starts_at"),
            field("latitude"),
            field("longitude")
        ).from("events")
         .where(field("latitude").between(south, north))
         .and(field("longitude").between(west, east))
         .orderBy(field("starts_at").desc())
         .limit(limit)
         .offset(offset)
         .fetch { it.intoMap() }

    fun listByRadius(lat: Double, lng: Double, radiusKm: Double, limit: Int = 200, offset: Int = 0): List<Map<String, Any?>> {
        val rad = 3.141592653589793 / 180.0
        val latField: Field<Double> = field("latitude", Double::class.java)
        val lngField: Field<Double> = field("longitude", Double::class.java)

        val expr = acos(
            sin(val_(lat * rad)).mul(sin(latField.mul(val_(rad)))) +
            cos(val_(lat * rad)).mul(cos(latField.mul(val_(rad)))).mul(
                cos(lngField.mul(val_(rad)).minus(val_(lng * rad)))
            )
        ).mul(val_(6371.0))

        return dsl.select(
            field("id"),
            field("title"),
            field("description"),
            field("starts_at"),
            latField.`as`("latitude"),
            lngField.`as`("longitude"),
            expr.`as`("distance_km")
        ).from("events")
         .where(expr.le(radiusKm))
         .orderBy(field("starts_at").desc())
         .limit(limit)
         .offset(offset)
         .fetch { it.intoMap() }
    }

    fun create(
        title: String,
        description: String?,
        startsAtIso: String,
        latitude: Double?,
        longitude: Double?,
        creatorId: Long
    ): Long {
        val id = dsl.insertInto(table("events"))
            .columns(
                field("title"),
                field("description"),
                field("starts_at"),
                field("latitude"),
                field("longitude"),
                field("creator_id")
            )
            .values(
                title,
                description,
                OffsetDateTime.parse(startsAtIso),
                latitude,
                longitude,
                creatorId
            )
            .returningResult(field("id", Long::class.java))
            .fetchOne()?.value1()
        return id ?: error("Failed to insert event")
    }
}
