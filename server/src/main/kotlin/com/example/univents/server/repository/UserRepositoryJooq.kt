package com.example.univents.server.repository

import org.jooq.DSLContext
import org.jooq.impl.DSL.*

class UserRepositoryJooq(private val dsl: DSLContext) {
    fun create(email: String, passwordHash: String): Long {
        val id = dsl.insertInto(table("users"))
            .columns(field("email"), field("password_hash"))
            .values(email, passwordHash)
            .returningResult(field("id", Long::class.java))
            .fetchOne()
            ?.value1()
        return id ?: error("Failed to insert user")
    }

    fun findByEmail(email: String): Map<String, Any?>? =
        dsl.select().from("users").where(field("email").eq(email)).fetchOne()?.intoMap()

    fun getIdByEmail(email: String): Long? =
        dsl.select(field("id")).from("users").where(field("email").eq(email)).fetchOne()?.get(0, Long::class.java)
}
