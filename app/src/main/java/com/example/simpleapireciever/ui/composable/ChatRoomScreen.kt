/*
 * Copyright 2025 Ivan Borzykh
 * SPDX-License-Identifier: MIT
 */

package com.example.simpleapireciever.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.simpleapireciever.ChatViewModel

@Composable
fun ChatRoomScreen(NavController: NavController, VM: ChatViewModel){
    val currentRoomId by VM.currentRoomId.collectAsState()

    DisposableEffect(currentRoomId) {
        if(currentRoomId!=-1) {
            VM.connectToChatRoom(currentRoomId)
            VM.getRoomData()
        }
        onDispose {
            if(currentRoomId!=-1) {
                VM.disconnectFromChatRoom()
            }
        }
    }

    val chatData by VM.currentRoomData.collectAsState()
    val chatMessages by VM.messages.collectAsState()
    val mainUser by VM.currentUser.collectAsState()
    var isDioalogShown by rememberSaveable {  mutableStateOf(false) }

    if(chatData!=null){
    val chatUsers =chatData!!.users
        var currentText by rememberSaveable { mutableStateOf("") }
        if(isDioalogShown==true){
        Dialog({isDioalogShown=false}) {
            Column {
            Button({VM.disconnectFromChatRoom()
                VM.deleteRoom()
                VM.clearRoom()
                NavController.navigate("ChatListScreen")
            }) {Text("Confirm") }
            Button({isDioalogShown=false }) {Text("Back") }
            }
        }}
        Scaffold() { innerPadding ->
    Column (modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().weight(0.1f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
            )
        {
            Button({ NavController.navigate("ChatListScreen") }) { Text("back") }
            Text("Chat with ${chatUsers.find { publicUser -> 
                publicUser.id!=VM.currentUser.value!!.id 
            }!!.name}")
            Button({
                isDioalogShown=true
            }) { Text("delete") }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(10.dp),

            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chatMessages.asReversed(),) { item, ->
                if (item.userid == mainUser!!.id) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End
                    ) {
                        Card(

                        ) {
                            Text(item.text, modifier = Modifier.padding(10.dp))
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Card() {
                            Text(
                                chatUsers.find { PublicUser ->
                                    (PublicUser.id == item.userid)
                                }!!.name,
                                style = TextStyle(fontSize = 10.sp,),
                                modifier = Modifier.padding(10.dp, 10.dp, 10.dp, 0.dp,)
                            )
                            Text(
                                item.text,
                                style = TextStyle(fontSize = 15.sp),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }

            }
        }
        Row(modifier = Modifier.fillMaxWidth().weight(0.1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(currentText, { currentText = it }, modifier = Modifier.fillMaxHeight())
            Button({
                if (currentText != "") {
                    VM.sendWebSocketMessage(currentText)
                    currentText = ""
                }
            }, modifier = Modifier.fillMaxHeight()) { Text("Send") }
        }
    }
        }
    }
}