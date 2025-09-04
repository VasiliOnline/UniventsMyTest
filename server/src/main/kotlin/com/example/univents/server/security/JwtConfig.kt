package com.example.univents.server.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

object JwtConfig {

    private const val secret = "super_secret_jwt_key"  // лучше из env
    private const val issuer = "univents-server"
    private const val validityInMs = 36_000_00 * 24   // 24 часа
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

