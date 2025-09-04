package com.example.univents.server.models

import org.jetbrains.exposed.dao.id.IntIdTable

// 📌 Таблица пользователей для БД
object Users : IntIdTable() {
    val email = varchar("email", 255).uniqueIndex() // email уникальный
    val password = varchar("password", 64)          // хэш пароля
}
