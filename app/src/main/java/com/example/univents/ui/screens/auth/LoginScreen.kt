package com.example.univents.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.univents.viewmodel.AuthViewModel
import com.example.univents.data.repository.AuthRepository
import com.example.univents.data.repository.FakeAuthRepository

// Фабрика для создания AuthViewModel вручную
class AuthViewModelFactory(
    private val useFakeRepo: Boolean
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (useFakeRepo) {
            AuthViewModel(fakeRepo = FakeAuthRepository()) as T
        } else {
            AuthViewModel(realRepo = AuthRepository()) as T
        }
    }
}

@Composable
fun LoginScreen(
    useFakeRepo: Boolean = true, // переключатель между фейковым и реальным репо
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current

    // создаём ViewModel через фабрику
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(useFakeRepo)
    )

    val authState by viewModel.authState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Войти")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { viewModel.register(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Регистрация")
        }
    }

    // реакция на изменение состояния
    LaunchedEffect(authState) {
        authState?.let { response ->
            Toast.makeText(context, "Вошли как: ${response.user.email}", Toast.LENGTH_SHORT).show()
            onLoginSuccess()
        }
    }
}
