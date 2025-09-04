package com.example.univents.data.repository

import com.example.univents.data.RetrofitInstance
import com.example.univents.models.AuthRequest
import com.example.univents.models.AuthResponse

// Реализация репозитория — использует Retrofit API
class AuthRepository {
    private val api = RetrofitInstance.authApi // получаем реализацию AuthApi

    // suspend — асинхронная функция, вызывается в корутине
    suspend fun login(email: String, password: String): AuthResponse {
        return api.login(AuthRequest(email, password)) // отправляем запрос и возвращаем ответ
    }

    suspend fun register(email: String, password: String): AuthResponse {
        return api.register(AuthRequest(email, password))
    }
}
