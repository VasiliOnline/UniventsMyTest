package com.example.server

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import io.ktor.server.application.*
import org.flywaydb.core.internal.database.base.Database

fun Application.configureDb() {
    val url = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/univents"
    val user = System.getenv("DB_USER") ?: "postgres"
    val pass = System.getenv("DB_PASSWORD") ?: "postgres"

    val cfg = HikariConfig().apply {
        jdbcUrl = url
        username = user
        password = pass
        maximumPoolSize = 5
    }
    val ds = HikariDataSource(cfg)
    Flyway.configure().dataSource(ds).locations("classpath:db/migration").load().migrate()
    Database.connect(ds)
}
