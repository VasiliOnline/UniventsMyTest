package com.example.univents.server

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import com.example.univents.server.db.DataSourceFactory
import com.example.univents.server.db.jooq
import com.example.univents.server.routes.authRoutes
import com.example.univents.server.routes.userRoutes
import com.example.univents.server.routes.eventRoutes

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

    val ds = DataSourceFactory.make()
    val dsl = jooq(ds)

    routing {
        get("/healthz") { call.respondText("OK") }
        authRoutes(dsl)
        userRoutes(dsl)
        eventRoutes(dsl)
    }
}
