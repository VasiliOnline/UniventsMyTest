package com.example.univents.server.routes

import com.example.univents.server.models.AuthRequest
import com.example.univents.server.models.AuthResponse
import com.example.univents.server.models.ErrorResponse
import com.example.univents.server.repository.UserRepository
import com.example.univents.server.security.JwtConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.mindrot.jbcrypt.BCrypt

fun Route.authRoutes(repo: UserRepository) {

    post("/auth/register") {
        val request = call.receive<AuthRequest>()
        val hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())
        val success = repo.addUser(request.email, hashedPassword)
        if (success) {
            call.respond(HttpStatusCode.Created, AuthResponse(
                token = null,
                message = "Пользователь успешно зарегистрирован",
                email = request.email
            ))
        } else {
            call.respond(HttpStatusCode.Conflict, ErrorResponse("USER_EXISTS", "Пользователь уже существует"))
        }
    }

    post("/auth/login") {
        val request = call.receive<AuthRequest>()
        val storedPassword = repo.findUserByEmail(request.email)
        if (storedPassword != null && BCrypt.checkpw(request.password, storedPassword)) {
            val token = JwtConfig.makeToken(request.email)
            call.respond(HttpStatusCode.OK, AuthResponse(
                token = token,
                message = "Успешный вход",
                email = request.email
            ))
        } else {
            call.respond(HttpStatusCode.Unauthorized, ErrorResponse("BAD_CREDENTIALS", "Неверные учетные данные"))
        }
    }
}
