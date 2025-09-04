package com.example.univents.server.routes

import com.example.univents.server.repository.EventRepository
import com.example.univents.server.models.UpdateEventRequest
import com.example.univents.server.models.DeleteEventRequest
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

data class CreateEventRequest(
    val title: String,
    val description: String,
    val date: String
)

@Suppress("SuspiciousIndentation")
fun Route.eventRoutes() {

    val repo = EventRepository()

        authenticate("auth-jwt") {

            post("/events/create") {
                val principal = call.principal<JWTPrincipal>()
                val email = principal?.getClaim("email", String::class)
                if (email == null) {
                    call.respond(mapOf("status" to "error", "message" to "Нет email в токене"))
                    return@post
                }

                val request = call.receive<CreateEventRequest>()
                val eventId = repo.createEvent(request.title, request.description, request.date, email)
                call.respond(mapOf(
                    "status" to "ok",
                    "eventId" to eventId
                ))
            }

            get("/events/mine") {
                val principal = call.principal<JWTPrincipal>()
                val email = principal?.getClaim("email", String::class)
                if (email == null) {
                    call.respond(mapOf("status" to "error", "message" to "Нет email в токене"))
                    return@get
                }

                val events = repo.getEventsByUser(email)
                call.respond(events)
            }
            // Редактирование события
            put("/events/update") {
                val principal = call.principal<JWTPrincipal>()
                val email = principal?.getClaim("email", String::class) ?: return@put call.respond(
                    mapOf("status" to "error", "message" to "Нет email в токене")
                )

                val request: UpdateEventRequest = call.receive()
                val success = repo.updateEvent(request.id, email, request.title, request.description, request.date)
                if (success) {
                    call.respond(mapOf("status" to "ok", "message" to "Событие обновлено"))
                } else {
                    call.respond(mapOf("status" to "error", "message" to "Событие не найдено или доступ запрещен"))
                }
            }

            // Удаление события
            delete("/events/delete") {
                val principal = call.principal<JWTPrincipal>()
                val email = principal?.getClaim("email", String::class) ?: return@delete call.respond(
                    mapOf("status" to "error", "message" to "Нет email в токене")
                )

                val request: DeleteEventRequest = call.receive()
                val success = repo.deleteEvent(request.id, email)
                if (success) {
                    call.respond(mapOf("status" to "ok", "message" to "Событие удалено"))
                } else {
                    call.respond(mapOf("status" to "error", "message" to "Событие не найдено или доступ запрещен"))
                }
            }
        }


        // Доступно всем, даже без токена
        get("/events") {
            val events = repo.getAllEvents()
            call.respond(events)
        }
}