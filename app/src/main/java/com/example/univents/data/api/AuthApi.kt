package com.example.univents.data.api

import com.example.univents.models.AuthRequest
import com.example.univents.models.AuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

// Интерфейс описывает сетевые запросы — Retrofit сам создаст реализацию.
interface AuthApi {
    // Похоже на аттрибут с возможностью выбора метода и url
    @POST("/auth/login") // HTTP POST к пути /auth/login на базовом URL
    suspend fun login(@Body request: AuthRequest): AuthResponse
    // suspend — значит функция асинхронная, её нужно вызывать в корутине

    @POST("/auth/register")
    suspend fun register(@Body request: AuthRequest): AuthResponse
}
