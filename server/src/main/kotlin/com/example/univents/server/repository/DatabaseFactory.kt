package com.example.univents.server.repository

import com.typesafe.config.ConfigFactory
import org.jetbrains.exposed.sql.Database
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import com.example.univents.server.models.Users
import com.example.univents.server.models.Events

object DatabaseFactory {
    fun init() {
        val config = ConfigFactory.load()
        val dbUrl = config.getString("database.url")
        val dbDriver = config.getString("database.driver")
        val dbUser = config.getString("database.user")
        val dbPassword = config.getString("database.password")

        val hikariConfig = HikariConfig().apply {
            jdbcUrl = dbUrl
            driverClassName = dbDriver
            username = dbUser
            password = dbPassword
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        val dataSource = HikariDataSource(hikariConfig)
        Database.connect(dataSource)

        // создаём таблицы, если их нет
        transaction {
            SchemaUtils.create(Users, Events)
        }
    }
}