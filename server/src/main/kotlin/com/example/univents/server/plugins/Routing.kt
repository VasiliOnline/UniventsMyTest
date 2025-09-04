package com.example.univents.server.plugins

import com.example.univents.server.repository.UserRepository
import io.ktor.server.routing.*
import io.ktor.server.application.*
import com.example.univents.server.routes.*

fun Application.configureRouting() {
    val userRepo = UserRepository()

    routing {
        authRoutes(userRepo)
        userRoutes()
        eventRoutes()
    }
}
