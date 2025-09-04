package com.example.univents.data

import com.example.univents.data.api.AuthApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance { // объект — синглтон: один экземпляр на приложение
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // логируем тело запросов/ответов — удобно для отладки
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging) // подключаем логгер к клиенту
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost:8080/") // TODO: замени на адрес твоего бэкенда (обязательно с "/")
        .addConverterFactory(GsonConverterFactory.create()) // конвертер JSON <-> объекты
        .client(client) // используем созданный OkHttp клиент
        .build()

    // заранее создаём реализацию AuthApi
    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java) // Retrofit генерирует класс по интерфейсу
    }
}
