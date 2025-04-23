/*
 * Copyright 2025 Ivan Borzykh
 * SPDX-License-Identifier: MIT
 */

package com.example.simpleapireciever

import android.util.Log
import com.example.simpleapireciever.NetworkModule.provideRestBaseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule{

    @Provides
    @Singleton
    fun provideTokenInterceptor(): TokenInterceptor = TokenInterceptor()

    @Provides
    @Singleton
    fun provideOkHttpClient(tokenInterceptor: TokenInterceptor): OkHttpClient {

        return OkHttpClient.Builder()
            .addInterceptor(tokenInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(provideRestBaseUrl())
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideNewsApi(retrofit: Retrofit): ChatApi {
        return retrofit.create(ChatApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNewsRepository(chatApi: ChatApi): Repository {
        return Repository(chatApi)
    }
}



@Module
@InstallIn(SingletonComponent::class)
object TokenManager {
    private var token: String? = null

    fun saveToken(newToken: String) {
        token = newToken
    }

    fun getToken(): String? = token
}

class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = TokenManager.getToken()

        Log.d("TokenInterceptor", "request sent: ${originalRequest.method()} ${originalRequest.url()}")

        if (!token.isNullOrEmpty()) {
            Log.d("TokenInterceptor", "Token found, add header Authorization")
        } else {
            Log.d("TokenInterceptor", "Not found, no header sent Authorization")
        }

        val newRequest = if (!token.isNullOrEmpty()) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        val response = chain.proceed(newRequest)

        Log.d("TokenInterceptor", "Received: HTTP ${response.code()}, URL: ${newRequest.url()}")

        return response
    }
}
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named("baseUrl")
    fun provideWebSocketBaseUrl(): String {

        return "ws://localhost:8000"

    }
    fun provideRestBaseUrl(): String {

        return "http://localhost:8000"

    }

}