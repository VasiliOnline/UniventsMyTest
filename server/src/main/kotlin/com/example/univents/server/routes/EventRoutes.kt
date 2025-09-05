package com.example.server

import com.example.server.models.EventDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.eventRoutes() {
    route("/api/v1/events") {
        get {
            // TODO: fetch from DB (stub for now)
            val list = listOf(
                EventDto(1,"Meetup Kotlin","...", "2025-09-06T18:00:00Z", 55.75, 37.62),
                EventDto(2,"Coffee Blind Date","скидка 20%", "2025-09-07T19:00:00Z", 55.76, 37.60),
            )
            call.respond(list)
        }
        authenticate {
            post {
                // TODO: insert into DB
                call.respond(HttpStatusCode.Created)
            }
            get("/mine") {
                call.respond(emptyList<EventDto>())
            }
        }
    }
}
