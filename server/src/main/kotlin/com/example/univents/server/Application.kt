package com.example.univents.server

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import com.example.univents.server.plugins.*
import com.example.univents.server.repository.DatabaseFactory
import com.typesafe.config.ConfigFactory
import io.ktor.server.application.call
import io.ktor.server.config.HoconApplicationConfig


import io.ktor.server.routing.*
import io.ktor.server.response.*


// 📌 Основная точка входа
fun main() {
    // Загружаем конфигурацию
    val config = HoconApplicationConfig(ConfigFactory.load())


    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {

        routing {
            get("/") {
                call.respondText("Hello, Univents!")
            }
        }
        DatabaseFactory.init()         // подключаем базу данных
        configureSerialization()       // JSON сериализация
        configureSecurity()            // JWT-аутентификация
        configureRouting()             // роуты
    }.start(wait = true)
}

