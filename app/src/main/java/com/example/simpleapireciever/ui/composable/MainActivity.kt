/*
 * Copyright 2025 Ivan Borzykh
 * SPDX-License-Identifier: MIT
 */

package com.example.simpleapireciever.ui.composable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.simpleapireciever.ChatViewModel
import com.example.simpleapireciever.ui.theme.BackgroundMain
import com.example.simpleapireciever.ui.theme.BackgroundSecondary


import com.example.simpleapireciever.ui.theme.SimpleApiRecieverTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleApiRecieverTheme {
                Surface(modifier = Modifier
                    .fillMaxSize()
                    .background(brush = Brush.horizontalGradient(colors = listOf(
                        BackgroundMain,
                        BackgroundSecondary)
                        )
                    )
                ) {
                    NavigationMain()
                }
            }
        }
    }
}
@Composable
fun NavigationMain(){
    val navController = rememberNavController()
    val viewModel: ChatViewModel = hiltViewModel()
    NavHost(navController = navController, startDestination = Screens.AuthScreen.route){
        composable(Screens.AuthScreen.route) {AuthAndLoginScreen(navController,viewModel) }
        composable(Screens.ChatListScreen.route) { ChatListScreen(navController,viewModel) }
        composable(Screens.ChatRoomScreen.route) { ChatRoomScreen(navController,viewModel) }
        composable(Screens.RegisterScreen.route) { RegistrationScreen(navController,viewModel) }
    }
}

sealed class Screens(val route: String){
    object AuthScreen:Screens("AuthAndLoginScreen")
    object RegisterScreen:Screens("RegistrationScreen")
    object ChatListScreen:Screens("ChatListScreen")
    object ChatRoomScreen:Screens("ChatRoomScreen")
}