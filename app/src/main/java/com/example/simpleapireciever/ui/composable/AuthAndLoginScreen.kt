package com.example.simpleapireciever.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.simpleapireciever.ChatViewModel
import com.example.simpleapireciever.ui.theme.PurpleGrey80

@Composable
fun AuthAndLoginScreen(NavController: NavController, VM: ChatViewModel){
    val currentUser by VM.currentUser.collectAsState()
    if (currentUser != null) {
        LaunchedEffect(currentUser) {
            NavController.navigate(Screens.ChatListScreen.route) {
                popUpTo(Screens.AuthScreen.route) { inclusive = true }
            }
        }
    }
    var currentTextName by rememberSaveable { mutableStateOf("") }
    var currentTextPass by rememberSaveable { mutableStateOf("") }
    var currentTextEmail by rememberSaveable { mutableStateOf("") }
    Scaffold() {innerPadding->
        Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally){
            Text("Username")
            TextField(currentTextName,{currentTextName=it})
            Text("Password")
            TextField(currentTextPass,{currentTextPass=it})
            Text("Email")
            TextField(currentTextEmail,{currentTextEmail=it})

            Button({
                VM.login(name = currentTextName, pass = currentTextPass, email = currentTextEmail)
                }
            ) {Text("Log in") }

            Text(
                modifier = Modifier.clickable {NavController.navigate("RegistrationScreen")},
                text = "register",
                color = PurpleGrey80
            )
        }
    }
}