package com.example.univents.server.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.example.univents.server.security.JwtConfig

fun Application.configureSecurity() {
    install(Authentication) {
        jwt("auth-jwt") {
            verifier(JwtConfig.verifier())
            validate { credential ->
                if (!credential.payload.getClaim("email").asString().isNullOrEmpty()) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}
