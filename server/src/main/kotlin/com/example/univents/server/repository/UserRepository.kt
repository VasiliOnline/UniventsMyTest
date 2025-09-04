package com.example.univents.server.repository

import com.example.univents.server.models.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository {
    fun addUser(email: String, passwordHash: String): Boolean {
        return transaction {
            if (Users.select { Users.email eq email }.empty()) {
                Users.insert {
                    it[Users.email] = email
                    it[Users.password] = passwordHash
                }
                true
            } else false
        }
    }


    fun findUserByEmail(email: String): String? {
        return transaction {
            Users.select { Users.email eq email }
                .map { it[Users.password] }
                .singleOrNull()
        }
    }
}