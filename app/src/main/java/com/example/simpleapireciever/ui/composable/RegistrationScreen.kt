/*
 * Copyright 2025 Ivan Borzykh
 * SPDX-License-Identifier: MIT
 */

package com.example.simpleapireciever.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.simpleapireciever.ChatViewModel

@Composable
fun RegistrationScreen(NavController: NavController, VM: ChatViewModel){
    var currentTextName by rememberSaveable { mutableStateOf("") }
    var currentTextPass by rememberSaveable { mutableStateOf("") }
    var currentTextEmail by rememberSaveable { mutableStateOf("") }
    Scaffold() {innerPadding->
        Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            ){
            Text("Username")
            TextField(currentTextName,{currentTextName=it})
            Text("Email")
            TextField(currentTextEmail,{currentTextEmail=it})
            Text("Password")
            TextField(currentTextPass,{currentTextPass=it})
            Button({
                VM.registration(currentTextName,currentTextEmail,currentTextPass)
                NavController.navigate("AuthAndLoginScreen")}) { Text("Create account") }
        }
    }
}