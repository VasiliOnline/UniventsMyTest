package com.example.univents

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.univents.data.repository.AuthRepository
import com.example.univents.ui.screens.LoginScreen
import com.example.univents.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // создаём ViewModel с Fake-репозиторием напрямую (просто и удобно для теста)
        val viewModel = AuthViewModel(realRepo = AuthRepository(), fakeRepo = null)

        setContent {
            LoginScreen(
                useFakeRepo = true, // пока тестируем на FakeAuthRepository
                onLoginSuccess = {
                    // Здесь можно перейти на следующий экран (например, MapScreen)
                    println("Успешный вход!")
                }
            )
        }
        //enableEdgeToEdge()
        //setContent {
       //     UniventsTheme {
       //         Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
      //              Greeting(
      //                  name = "Android",
      //                  modifier = Modifier.padding(innerPadding)
      //              )
     //           }
     //       }
     //   }
    }
}

//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
 //   )
//}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
   // UniventsTheme {
  //      Greeting("Android")
   // }
        //}