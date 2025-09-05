package com.example.univents.server.routes

import com.example.univents.server.models.EventDto
import com.example.univents.server.repository.EventRepositoryJooq
import com.example.univents.server.repository.UserRepositoryJooq
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jooq.DSLContext

@kotlinx.serialization.Serializable
data class CreateEventRequest(
    val title: String,
    val description: String? = null,
    val dateIso: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)

fun Route.eventRoutes(dsl: DSLContext) {
    val repo = EventRepositoryJooq(dsl)
    val users = UserRepositoryJooq(dsl)

    route("/api/v1/events") {
        get {
            val bbox = call.request.queryParameters["bbox"]
            val lat = call.request.queryParameters["lat"]?.toDoubleOrNull()
            val lng = call.request.queryParameters["lng"]?.toDoubleOrNull()
            val radius = call.request.queryParameters["radius"]?.toDoubleOrNull()
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 200
            val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0

            val rows = when {
                bbox != null -> {
                    val parts = bbox.split(",", limit = 4).mapNotNull { it.trim().toDoubleOrNull() }
                    if (parts.size != 4) return@get call.respond(HttpStatusCode.BadRequest, "Invalid bbox")
                    repo.listByBbox(parts[0], parts[1], parts[2], parts[3], limit, offset)
                }
                lat != null && lng != null && radius != null -> {
                    repo.listByRadius(lat, lng, radius, limit, offset)
                }
                else -> repo.listAll(limit, offset)
            }

            val list = rows.map {
                EventDto(
                    id = (it["id"] as Number).toLong(),
                    title = it["title"] as String,
                    description = it["description"] as String?,
                    date = it["starts_at"]?.toString() ?: "",
                    latitude = (it["latitude"] as? Number)?.toDouble(),
                    longitude = (it["longitude"] as? Number)?.toDouble()
                )
            }
            call.respond(list)
        }

        authenticate {
            post {
                val principal = call.principal<JWTPrincipal>() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val email = principal.payload.getClaim("email").asString()
                val userId = users.getIdByEmail(email) ?: return@post call.respond(HttpStatusCode.Forbidden, "User not found")

                val req = call.receive<CreateEventRequest>()
                if (req.title.isBlank()) return@post call.respond(HttpStatusCode.BadRequest, "title is required")

                val id = repo.create(
                    title = req.title,
                    description = req.description,
                    startsAtIso = req.dateIso,
                    latitude = req.latitude,
                    longitude = req.longitude,
                    creatorId = userId
                )
                call.respond(HttpStatusCode.Created, mapOf("id" to id))
            }
        }
    }
}
