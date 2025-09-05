package com.example.server

import com.example.server.models.AuthRequest
import com.example.server.models.AuthResponse
import com.sun.tools.javac.util.Name
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import org.mindrot.jbcrypt.BCrypt

object Users : Table("users") {
    val id = long("id").autoIncrement()
    val email = text("email").uniqueIndex()
    val passwordHash = text("password_hash")
    val createdAt = datetime("created_at")
    override val primaryKey = PrimaryKey(id)
}

fun Route.authRoutes() {
    route("/api/v1/auth") {
        post("/register") {
            val req = call.receive<AuthRequest>()
            val secret = System.getenv("JWT_SECRET") ?: "dev"
            val hashed = BCrypt.hashpw(req.password, BCrypt.gensalt())
            try {
                transaction {
                    SchemaUtils.create(Users)
                    Users.insert {
                        it[email] = req.email
                        it[passwordHash] = hashed
                        it[createdAt] = LocalDateTime.now()
                    }
                }
            } catch (e: Exception) {
                return@post call.respond(HttpStatusCode.Conflict, "Email already exists")
            }
            val token = Jwt.sign(req.email, secret)
            call.respond(AuthResponse(token))
        }
        post("/login") {
            val req = call.receive<AuthRequest>()
            val secret = System.getenv("JWT_SECRET") ?: "dev"
            val record = transaction {
                Users.select { Users.email eq req.email }.singleOrNull()
            }
            if (record == null || !BCrypt.checkpw(req.password, record[Users.passwordHash])) {
                return@post call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
            }
            val token = Jwt.sign(req.email, secret)
            call.respond(AuthResponse(token))
        }
    }
}
