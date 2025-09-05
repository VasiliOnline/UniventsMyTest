package com.example.univents.server.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

object JwtConfig {
    private val secret = System.getenv("JWT_SECRET") ?: "dev_super_secret_change_me"
    private const val issuer = "univents-server"
    private const val validityInMs = 86_400_000 // 24h
    private val algorithm = Algorithm.HMAC512(secret)

    fun makeToken(email: String): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim("email", email)
        .withExpiresAt(Date(System.currentTimeMillis() + validityInMs))
        .sign(algorithm)

    fun verifier(): JWTVerifier = JWT.require(algorithm)
        .withIssuer(issuer)
        .build()
}
