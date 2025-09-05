package com.example.univents.server

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

object Jwt {
    fun makeVerifier(secret: String) = JWT.require(Algorithm.HMAC256(secret)).build()
    fun sign(email: String, secret: String, ttlMs: Long = 7*24*3600_000L): String =
        JWT.create()
            .withClaim("email", email)
            .withExpiresAt(Date(System.currentTimeMillis() + ttlMs))
            .sign(Algorithm.HMAC256(secret))
}
