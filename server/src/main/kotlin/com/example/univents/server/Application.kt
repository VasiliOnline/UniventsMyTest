package com.example.server

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

fun main() = EngineMain.main(emptyArray())

fun Application.module() {
    install(ContentNegotiation) { json() }
    install(Authentication) {
        jwt {
            val secret = System.getenv("JWT_SECRET") ?: "dev"
            verifier(Jwt.makeVerifier(secret))
            validate { cred ->
                val email = cred.payload.getClaim("email").asString()
                if (!email.isNullOrBlank()) JWTPrincipal(cred.payload) else null
            }
        }
    }
    configureDb()
    routing {
        get("/healthz") { call.respondText("OK") }
        authRoutes()
        userRoutes()
        eventRoutes()
    }
}
