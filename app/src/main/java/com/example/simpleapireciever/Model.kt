/*
 * Copyright 2025 Ivan Borzykh
 * SPDX-License-Identifier: MIT
 */

package com.example.simpleapireciever

import kotlinx.serialization.Serializable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface ChatApi{

    @GET("getUsers")
    suspend fun getUsers():List<PublicUser>

    @POST("deleteroom")
    suspend fun deleteRoom(@Body roomRequest: RoomRequest): Response<ResponseBody>

    @POST("createroom")
    suspend fun createRoom(@Body targetUser: PublicUser):Int

    @GET("getrooms")
    suspend fun getRooms():List<ChatRoom>

    @GET("getroomdata/{chatRoomId}")
    suspend fun getRoomData(
        @Path("chatRoomId") chatRoomId: Int
    ): RoomData

    @POST("auth")
    suspend fun auth(@Body user:User):TokenResponse

    @POST("postmessage")
    suspend fun postMessage(@Body message:Message)

    @POST("register")
    suspend fun register(@Body user:User)

    @GET("ping")
    suspend fun ping(): Response<ResponseBody>

    @GET("getmyuserdata")
    suspend fun getUserData(): PublicUser
}

data class User(
    val id:Int,
    val name:String,
    val email:String,
    val pass:String
)
@Serializable
data class PublicUser(
    val id:Int,
    val name:String
)

data class RoomData(
    val users: List<PublicUser>,
    val messages: List<Message>
)

@Serializable
data class Message (
    val id:Int,
    val userid:Int,
    val chatId:Int,
    val isRead:Boolean,
    val insertedDate:Long,
    val text:String,
)
@Serializable
data class ChatRoomList(
    val chatRoomList: List<ChatRoom>
)

@Serializable
data class ChatRoom (
    val id:Int,
    val isGroup:Boolean,
    val name:String
)

data class RoomRequest(
    val roomId:Int
)
@Serializable
data class TokenResponse(val token: String)
