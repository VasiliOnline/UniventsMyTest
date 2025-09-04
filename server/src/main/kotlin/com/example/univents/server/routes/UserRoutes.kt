package com.example.univents.server.routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes() {

    authenticate("auth-jwt") {

            get("/profile") {

                val principal = call.principal<JWTPrincipal>()
                val email = principal?.getClaim("email", String::class)
                call.respond(mapOf(
                    "email" to email,
                    "message" to "Добро пожаловать в профиль!"
                ))
            }
        }
}
