package com.example.univents.server.plugins

import com.example.univents.server.repository.UserRepository
import com.example.univents.server.repository.EventRepository
import io.ktor.server.routing.*
import io.ktor.server.application.*
import com.example.univents.server.routes.*

fun Application.configureRouting() {
    val userRepo = UserRepository()
    val eventRepo = EventRepository()

    routing {
        authRoutes(userRepo)
        userRoutes()
        eventRoutes(eventRepo)
    }
}
