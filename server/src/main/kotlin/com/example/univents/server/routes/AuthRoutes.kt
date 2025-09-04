package com.example.univents.server.routes

import com.example.univents.server.models.AuthRequest
import com.example.univents.server.models.AuthResponse
import com.example.univents.server.repository.UserRepository
import com.example.univents.server.security.JwtConfig
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.mindrot.jbcrypt.BCrypt

@Suppress("SuspiciousIndentation")
fun Route.authRoutes(repo: UserRepository) {

    //val repo = UserRepository()


        post("/auth/register") {
            val request = call.receive<AuthRequest>()
            val hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())
            val success = repo.addUser(request.email, hashedPassword)
            if (success) {
                call.respond(
                    AuthResponse(
                        token = null,
                        message = "Пользователь успешно зарегистрирован",
                        email = request.email
                    )
                )
            }
            else {
                call.respond(
                    AuthResponse(
                        token = null,
                        message = "Пользователь уже существует",
                        email = null
                    )
                )
            }
        }

        post("/auth/login") {
            val request = call.receive<AuthRequest>()
            val storedPassword = repo.findUserByEmail(request.email)
            if (storedPassword != null && BCrypt.checkpw(request.password, storedPassword)) {
                val token = JwtConfig.makeToken(request.email)
                call.respond(
                    AuthResponse(
                        token = token,
                        message = "Успешный вход",
                        email = request.email
                    )
                )
            }
            else {
                call.respond(
                    AuthResponse(
                        token = null,
                        message = "Неверные учетные данные",
                        email = null
                    )
                )
            }
        }

}
