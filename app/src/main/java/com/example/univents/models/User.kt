
// указываем пакет (папку), где лежит файл
package com.example.univents.models

    // data class — удобная структура для хранения данных; Kotlin сгенерирует toString, equals и т.д.
    data class User(
        val id: String,          // уникальный идентификатор пользователя (строка)
        val email: String,       // email пользователя
        val name: String,        // отображаемое имя
        val token: String? = null // токен авторизации (может быть null)
    )
