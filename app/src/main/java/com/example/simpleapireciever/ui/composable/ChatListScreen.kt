/*
 * Copyright 2025 Ivan Borzykh
 * SPDX-License-Identifier: MIT
 */

package com.example.simpleapireciever.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.simpleapireciever.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(NavController: NavController, VM: ChatViewModel){
    val sheetState = rememberModalBottomSheetState()
    LaunchedEffect(Unit){
        VM.getUsers()
        VM.connectToRoomList()
    }
    DisposableEffect(VM.currentUser) {
        onDispose {
                VM.disconnectFromRoomListener()
        }
    }
    val chatList by VM.UserRoomList.collectAsState()
    val userList by VM.publicUserList.collectAsState()
    var isUserListShown by remember { mutableStateOf(false) }

    if(isUserListShown){
        ModalBottomSheet(onDismissRequest = {isUserListShown=false},sheetState= sheetState) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically){
            Text("Users") }
            LazyColumn(modifier =Modifier.fillMaxSize(),
            ) {
                items(userList){item ->
                    Column(modifier =Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,) {
                        OutlinedButton({VM.createRoom(item)
                            isUserListShown=false
                            VM.getRoomList()
                        }
                        ) { Text(item.name, color = Color.Black) }
                    }
                }
            }
        }
    }
    Scaffold() { innerPadding ->
        Column() {
            Button(onClick = {
                isUserListShown = true
            }) { Text("create room") }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(chatList) { item ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Button({
                            VM.changeRoom(item)
                            NavController.navigate("ChatRoomScreen")
                        }, modifier = Modifier.fillMaxWidth())
                        { Text(item.name) }
                    }
                }
            }
        }
    }
}