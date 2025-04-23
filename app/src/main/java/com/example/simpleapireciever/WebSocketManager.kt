/*
 * Copyright 2025 Ivan Borzykh
 * SPDX-License-Identifier: MIT
 */

package com.example.simpleapireciever

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Named

class WebSocketManager @Inject constructor(
    private val okHttpClient: OkHttpClient,
    @Named("baseUrl") private val baseUrl: String
) {
    private var webSocketRoom: WebSocket? = null
    private var webSocketRoomList: WebSocket? = null

    fun openChatSocket(chatRoomId: Int, listener: WebSocketListener) {
        val url = "$baseUrl/ws/$chatRoomId"
        val request = Request.Builder()
            .url(url)
            .build()
        webSocketRoom = okHttpClient.newWebSocket(request, listener)
    }

    fun closeChatSocket(code: Int = 1000, reason: String? = null) {
        webSocketRoom?.close(code, reason)
    }

    fun sendMessage(message: String) {
        webSocketRoom?.send(message)
    }
    fun openChatRoomsSocket(listener: WebSocketListener){
        val url = "$baseUrl/ws/userchatrooms"
        val request = Request.Builder()
            .url(url)
            .build()
        webSocketRoomList = okHttpClient.newWebSocket(request, listener)
    }
    fun closeChatRoomSocket(code: Int = 1000, reason: String? = null) {
        webSocketRoomList?.close(code, reason)
    }
}