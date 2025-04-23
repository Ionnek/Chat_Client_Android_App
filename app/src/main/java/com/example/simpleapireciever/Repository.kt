/*
 * Copyright 2025 Ivan Borzykh
 * SPDX-License-Identifier: MIT
 */

package com.example.simpleapireciever

import android.util.Log
import javax.inject.Inject

class Repository @Inject constructor(
    private val chatApi: ChatApi
) {
    suspend fun createRoom(targetUser:PublicUser): Int {
        val result = chatApi.createRoom(targetUser)
        Log.d ("result","created room $result")
        return result
    }
    suspend fun getUsers(): List<PublicUser> {
        Log.d ("users get","sent")
        val result = chatApi.getUsers()
        Log.d ("result","users recieved $result")
        return result
    }
    suspend fun getRoomData(roomId:Int): RoomData {
        val result = chatApi.getRoomData(roomId)
        Log.d ("result","room data recieved $result")
        return result
    }
    suspend fun deleteRoom(roomId: Int){
        val data = RoomRequest(roomId)
        val result = chatApi.deleteRoom(data)
        Log.d ("result","room $result")
    }
    suspend fun postMessage(message: Message) {
        val result = chatApi.postMessage(message)
        Log.d ("result","message $result")
        return result
    }
    suspend fun auth(user:User):TokenResponse{
        val result = chatApi.auth(user)
        TokenManager.saveToken(result.token)
        Log.d ("AUTH","arrive token${result.token}")
        return result
    }
    suspend fun register(user:User){
        chatApi.register(user)
        Log.d ("register","user sent ${user}")
    }
    suspend fun getRooms():List<ChatRoom>{
        Log.d ("get Rooms","sent")
        val result =chatApi.getRooms()
        Log.d ("get Rooms","arrived rooms")
        return result
    }
    suspend fun getUserData():PublicUser{
        Log.d ("register","try user id sent")
        val result = chatApi.getUserData()
        return result
    }
}
