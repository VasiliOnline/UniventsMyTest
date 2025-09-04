package com.example.univents.models

// DTO — ответ сервера после логина/регистрации
data class AuthResponse(
    val user: User,   // объект User (см. выше)
    val token: String // токен, который сервер вернул
)
