package com.example.univents.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.univents.data.repository.AuthRepository
import com.example.univents.data.repository.FakeAuthRepository
import com.example.univents.models.AuthResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ViewModel: держит состояние авторизации и вызывает репозиторий
// По умолчанию использует реальный репозиторий; для теста можно передать FakeAuthRepository
class AuthViewModel(
    private val realRepo: AuthRepository? = null, // реальный репозиторий (можно не передавать)
    private val fakeRepo: FakeAuthRepository? = null // если передан, будет использоваться он
) : ViewModel() {

    // внутреннее изменяемое состояние (можно читать как flow)
    private val _authState = MutableStateFlow<AuthResponse?>(null)
    val authState: StateFlow<AuthResponse?> = _authState // доступ только для чтения вне класса

    // публичная функция логина — её вызывает UI
    fun login(email: String, password: String) {
        viewModelScope.launch { // запускаем корутину в scope ViewModel
            try {
                val response = if (fakeRepo != null) {
                    //// используем Fake-репозиторий (локальная отладка)
                    fakeRepo.login(email, password)
                } else {
                    // используем реальный репозиторий
                    realRepo!!.login(email, password)
                }

                _authState.value = response // обновляем состояние (подписчики узнают)
                Log.d("AuthViewModel", "Login success: ${response.user.email}") // лог для проверки
            } catch (e: Exception) {
                _authState.value = null
                Log.e("AuthViewModel", "Login failed: ${e.message}")
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = if (fakeRepo != null) {
                    fakeRepo.register(email, password)
                } else {
                    realRepo!!.register(email, password)
                }
                _authState.value = response
                Log.d("AuthViewModel", "Register success: ${response.user.email}")
            } catch (e: Exception) {
                _authState.value = null
                Log.e("AuthViewModel", "Register failed: ${e.message}")
            }
        }
    }
}
