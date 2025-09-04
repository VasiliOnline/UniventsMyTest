package com.example.univents.models

// DTO — объект запроса для логина/регистрации
data class AuthRequest(
    val email: String,    // email для отправки на сервер
    val password: String  // пароль
)
