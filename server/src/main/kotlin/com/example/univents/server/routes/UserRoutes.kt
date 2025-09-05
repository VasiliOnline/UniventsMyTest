package com.example.server

import com.example.server.models.MeDto
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes() {
    authenticate {
        get("/api/v1/me") {
            val email = call.principal<JWTPrincipal>()!!.payload.getClaim("email").asString()
            call.respond(MeDto(email = email, displayName = email.substringBefore("@")))
        }
    }
}
