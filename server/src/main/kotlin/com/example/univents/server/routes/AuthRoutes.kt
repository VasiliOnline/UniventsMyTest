package com.example.univents.server.routes

import com.example.univents.server.Jwt
import com.example.univents.server.models.AuthRequest
import com.example.univents.server.models.AuthResponse
import com.example.univents.server.repository.UserRepositoryJooq
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jooq.DSLContext
import org.mindrot.jbcrypt.BCrypt

fun Route.authRoutes(dsl: DSLContext) {
    val users = UserRepositoryJooq(dsl)

    route("/api/v1/auth") {
        post("/register") {
            val req = call.receive<AuthRequest>()
            val hashed = BCrypt.hashpw(req.password, BCrypt.gensalt())
            try {
                users.create(req.email, hashed)
                val token = Jwt.sign(req.email, System.getenv("JWT_SECRET") ?: "dev")
                call.respond(AuthResponse(token))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, "Email already exists")
            }
        }
        post("/login") {
            val req = call.receive<AuthRequest>()
            val record = users.findByEmail(req.email)
            if (record == null || !BCrypt.checkpw(req.password, record["password_hash"] as String)) {
                return@post call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
            }
            val token = Jwt.sign(req.email, System.getenv("JWT_SECRET") ?: "dev")
            call.respond(AuthResponse(token))
        }
    }
}
