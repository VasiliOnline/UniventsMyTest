package com.example.univents.server

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL

object Database {
    private val dataSource by lazy {
        val cfg = HikariConfig().apply {
            jdbcUrl = System.getenv("DB_URL") ?: "jdbc:postgresql://127.0.0.1:5432/univents"
            username = System.getenv("DB_USER") ?: "postgres"
            password = System.getenv("DB_PASSWORD") ?: "postgres"
            maximumPoolSize = 10
        }
        HikariDataSource(cfg)
    }

    val dsl: DSLContext by lazy {
        DSL.using(dataSource, SQLDialect.POSTGRES)
    }
}

