/*
 * Copyright 2025 Ivan Borzykh
 * SPDX-License-Identifier: MIT
 */

package com.example.simpleapireciever

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: Repository,
    private val webSocketManager: WebSocketManager
):ViewModel(){

    var token by mutableStateOf<String?>(null)
        private set

    val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    val _publicUserList = MutableStateFlow<List<PublicUser>>(emptyList())
    val publicUserList: StateFlow<List<PublicUser>> = _publicUserList

    val _currentRoomId = MutableStateFlow<Int>(-1)
    val currentRoomId: StateFlow<Int> = _currentRoomId

    val _UserRoomList = MutableStateFlow<List<ChatRoom>>(emptyList())
    val UserRoomList: StateFlow<List<ChatRoom>> = _UserRoomList

    val _currentRoomData = MutableStateFlow<RoomData?>(null)
    val currentRoomData: StateFlow<RoomData?> = _currentRoomData

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    fun login(name:String,email:String,pass:String){
        viewModelScope.launch {
            try {
                var user = User(name = name, email = email, pass = pass, id = 0)
                TokenManager.saveToken(repository.auth(user).token)
                token=TokenManager.getToken()
                val userId =repository.getUserData().id
                user = user.copy(id=userId)
                _currentUser.value= user
            }catch (e:Exception){
                Log.d("User","Error $e")
            }
        }
    }
    fun getUsers(){
        viewModelScope.launch {
            try {
                _publicUserList.value=repository.getUsers()
            }catch (e:Exception){
                Log.d("Users list","Error $e")
            }
        }
    }
    fun registration(name:String,email:String,pass:String){
        viewModelScope.launch {
            try {
                var user = User(name = name, email = email, pass = pass, id = 0)
                repository.register(user)
            }catch (e:Exception){
                Log.d("Register","RegisterFailed")
            }
        }
    }
    fun getRoomList(){
        viewModelScope.launch {
            try {
               _UserRoomList.value =repository.getRooms()
            }catch (e:Exception){
                Log.d("getRooms","Failed")
            }
        }
    }
    fun createRoom(targetUser:PublicUser){
        viewModelScope.launch {
            try {
                _currentRoomId.value=repository.createRoom(targetUser)
            }catch (e:Exception){
                Log.d("Register","RegisterFailed")
            }
        }
    }
    fun getRoomData(){
        viewModelScope.launch {
            try {
                val roomData = repository.getRoomData(currentRoomId.value)
                _currentRoomData.value = roomData
                _messages.value = roomData.messages
            } catch (e: Exception) {
                Log.d("Register","RegisterFailed")
            }
        }
    }
    fun clearRoom(){
        _currentRoomData.value=null
        _currentRoomId.value=-1

    }
    fun changeRoom(chatRoom: ChatRoom){
        _currentRoomId.value=chatRoom.id
        getRoomData()
    }

    fun connectToRoomList() {
        webSocketManager.openChatRoomsSocket(socketRoomListener)
    }
    fun disconnectFromRoomListener() {
        webSocketManager.closeChatRoomSocket()
    }
    fun connectToChatRoom(roomId: Int) {
        webSocketManager.openChatSocket(roomId, socketMessageListener)
    }
    fun disconnectFromChatRoom() {
        webSocketManager.closeChatSocket()
    }
    fun deleteRoom(){
        viewModelScope.launch {
            repository.deleteRoom(currentRoomId.value)
        }
    }
    fun sendWebSocketMessage(text: String) {
        viewModelScope.launch {
            val msg = Message(
                id = 0,
                userid = currentUser.value!!.id,
                chatId = currentRoomId.value,
                isRead = false,
                insertedDate = System.currentTimeMillis(),
                text = text
            )
            val jsonMessage = Json.encodeToString(msg) // превращаем в JSON
            webSocketManager.sendMessage(jsonMessage)
        }
    }

    //---------------------------------------------------------------------------------------

    private val socketMessageListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d("ChatSocket", "Connected to room")
        }
        override fun onMessage(webSocket: WebSocket, text: String) {
            try {
                val incomingMessage = Json.decodeFromString<Message>(text)
                val currentList = _messages.value.toMutableList()
                currentList.add(incomingMessage)
                _messages.value = currentList
            } catch (e: Exception) {
                Log.e("ChatSocket", "Skipping non-JSON message: $text")
            }
        }
        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e("ChatSocket", "Error: ${t.message}")
        }
    }
    val socketRoomListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d("ChatSocket", "Connected to ws listner")
        }
        override fun onMessage(webSocket: WebSocket, text: String) {
            try {
                val incomingMessage = Json.decodeFromString<ChatRoomList>(text)
                _UserRoomList.value=incomingMessage.chatRoomList
            } catch (e: Exception) {
                Log.e("ChatSocket", "Skipping non-JSON message: $text")
            }
        }
        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e("ChatSocket", "Error: ${t.message}")
        }
    }
}