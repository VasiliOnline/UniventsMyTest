package com.example.univents.server.routes

import com.example.univents.server.models.*
import com.example.univents.server.repository.EventRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.eventRoutes(repo: EventRepository = EventRepository()) {

    route("/events") {

        get {
            val north = call.request.queryParameters["north"]?.toDoubleOrNull()
            val south = call.request.queryParameters["south"]?.toDoubleOrNull()
            val east = call.request.queryParameters["east"]?.toDoubleOrNull()
            val west = call.request.queryParameters["west"]?.toDoubleOrNull()
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 200
            val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0

            val events = repo.getAllEvents(north, south, east, west, limit, offset)
            call.respond(events)
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("BAD_ID", "Неверный id"))
            val event = repo.getById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound, ErrorResponse("NOT_FOUND", "Событие не найдено"))
            call.respond(event)
        }

        authenticate("auth-jwt") {

            post {
                val principal = call.principal<JWTPrincipal>()
                val email = principal?.getClaim("email", String::class)
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, ErrorResponse("NO_EMAIL", "Нет email в токене"))

                val request = call.receive<CreateEventRequest>()
                val id = repo.createEvent(
                    title = request.title,
                    description = request.description,
                    dateIso = request.date,
                    latitude = request.latitude,
                    longitude = request.longitude,
                    creatorEmail = email
                )
                val created = repo.getById(id)!!
                call.respond(HttpStatusCode.Created, created)
            }

            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, ErrorResponse("BAD_ID", "Неверный id"))
                val principal = call.principal<JWTPrincipal>()
                val email = principal?.getClaim("email", String::class)
                    ?: return@put call.respond(HttpStatusCode.Unauthorized, ErrorResponse("NO_EMAIL", "Нет email в токене"))

                val request = call.receive<UpdateEventRequest>()
                val ok = repo.updateEvent(
                    id = id,
                    requesterEmail = email,
                    title = request.title,
                    description = request.description,
                    dateIso = request.date,
                    latitude = request.latitude,
                    longitude = request.longitude
                )
                if (ok) call.respond(HttpStatusCode.OK)
                else call.respond(HttpStatusCode.Forbidden, ErrorResponse("FORBIDDEN_OR_MISSING", "Не найдено или нет прав"))
            }

            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ErrorResponse("BAD_ID", "Неверный id"))
                val principal = call.principal<JWTPrincipal>()
                val email = principal?.getClaim("email", String::class)
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, ErrorResponse("NO_EMAIL", "Нет email в токене"))

                val ok = repo.deleteEvent(id, email)
                if (ok) call.respond(HttpStatusCode.NoContent)
                else call.respond(HttpStatusCode.Forbidden, ErrorResponse("FORBIDDEN_OR_MISSING", "Не найдено или нет прав"))
            }
        }
    }
}
