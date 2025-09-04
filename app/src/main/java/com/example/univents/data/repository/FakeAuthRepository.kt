package com.example.univents.data.repository

import com.example.univents.models.AuthResponse
import com.example.univents.models.User
import kotlinx.coroutines.delay

// Fake-репозиторий — используется для локальной отладки без реального сервера
class FakeAuthRepository {
    // имитация задержки сети + возврат фиктивного ответа
    suspend fun login(email: String, password: String): AuthResponse {
        delay(800) // имитируем сетевую задержку (800 мс)
        val user = User(id = "1", email = email, name = "Test User", token = "fake_token_123")
        return AuthResponse(user = user, token = "fake_token_123")
    }

    suspend fun register(email: String, password: String): AuthResponse {
        delay(1000)
        val user = User(id = "2", email = email, name = "New User", token = "fake_token_456")
        return AuthResponse(user = user, token = "fake_token_456")
    }
}
