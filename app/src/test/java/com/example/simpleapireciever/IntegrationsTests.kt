package com.example.simpleapireciever

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class IntegrationsTests {
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var mockWebServer: MockWebServer
    private lateinit var retrofit: Retrofit
    private lateinit var chatApi: ChatApi
    private lateinit var repository: Repository
    private lateinit var viewModel: ChatViewModel

    val token = TokenResponse(token = "123")
    val userPublic=PublicUser(id = 1, name="TestUser")
    val user=User(id = 1, name="TestUser",pass="password123", email = "test@example.com")
    val userList = listOf(userPublic, userPublic.copy(id=2, name = "test@example.com"))
    val testChatRooms = listOf(
        ChatRoom(id = 1,isGroup = false, name = "Room 1"),
        ChatRoom(id = 2,isGroup = false, name = "Room 2")
    )
    val chatRoomList = ChatRoomList(chatRoomList = testChatRooms)
    val messageTest=Message(id = 1, userid = 1, chatId = 1, isRead = false, insertedDate = 0, text = "testText")

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockWebServer = MockWebServer()
        mockWebServer.start()
        val okhhtp=OkHttpClient.Builder().addInterceptor(TokenInterceptor()).build()
        val httpUrl = mockWebServer.url("/")
        val wsUrl = httpUrl.toString().replace("http://", "ws://")
        retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(okhhtp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val webSocketManager = WebSocketManager(okhhtp,wsUrl.dropLast(1) )

        chatApi = retrofit.create(ChatApi::class.java)
        repository=Repository(chatApi)
        viewModel = ChatViewModel(repository,webSocketManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mockWebServer.shutdown()
    }

    //connection and registration test
    @Test
    fun testRegistrationRequestFormat(): Unit = runBlocking {

        val mockResponse = MockResponse()
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)
        viewModel.registration("TestUser", "test@example.com", "password123")

        val recordedRequest = mockWebServer.takeRequest(5, TimeUnit.SECONDS)
        assertNotNull("registration request not found", recordedRequest)
        assertEquals("POST", recordedRequest!!.method)
        assertEquals("/register", recordedRequest.path)

        val requestBody = recordedRequest.body.readUtf8()
        assertTrue("not found", requestBody.contains("\"name\":\"TestUser\""))
        assertTrue("email not found", requestBody.contains("\"email\":\"test@example.com\""))
        assertTrue("pass not found", requestBody.contains("\"pass\":\"password123\""))
    }

    //auth and token managment test
    @Test
    fun testAuth():Unit = runBlocking {
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return when (request.path) {
                    "/auth" -> {
                        MockResponse()
                            .setResponseCode(200)
                            .setBody(Json.encodeToString(token))
                    }
                    "/getmyuserdata" -> {
                        val token = request.getHeader("Authorization")
                        if (token == "Bearer 123") {
                            MockResponse()
                                .setResponseCode(200)
                                .setBody(Json.encodeToString(userPublic))
                        } else {
                            MockResponse()
                                .setResponseCode(401)
                                .setBody("{\"error\":\"Unauthorized\"}")
                        }
                    }
                    "/getUsers" -> {
                        MockResponse()
                            .setResponseCode(200)
                            .setBody(Json.encodeToString(userList))
                    }

                    else -> {
                        MockResponse().setResponseCode(404)
                    }
                }
            }
        }
        viewModel.login("TestUser", "test@example.com", "password123")

        delay(100)

        assertEquals("Token is not set correctly", "123", TokenManager.getToken())

        val authRequest = mockWebServer.takeRequest(5, TimeUnit.SECONDS)
        assertNotNull("login (auth) request not found", authRequest)
        assertEquals("POST", authRequest!!.method)
        assertEquals("/auth", authRequest.path)

        val userDataRequest = mockWebServer.takeRequest(5, TimeUnit.SECONDS)
        assertNotNull("getmyuserdata request not found", userDataRequest)
        assertEquals("/getmyuserdata", userDataRequest!!.path)
        assertEquals("Bearer 123", userDataRequest.getHeader("Authorization"))
        //in view model from puplic user takes id and updates main user(User class)
        assertEquals(user, viewModel.currentUser.value)

        delay(100)

        viewModel.getUsers()

        delay(100)

        val usersRequest = mockWebServer.takeRequest(5, TimeUnit.SECONDS)

        assertNotNull("getusers request not found", usersRequest)
        assertEquals("/getUsers", usersRequest!!.path)
        assertEquals("Bearer 123", usersRequest.getHeader("Authorization"))
        assertEquals(userList, viewModel.publicUserList.value)
    }

    //room list listener websocket test
    @Test
    fun testRoomListWS() = runBlocking {
        val jsonMessage = Json.encodeToString(chatRoomList)
        val mockResponse = MockResponse()
            .withWebSocketUpgrade(object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    webSocket.send(jsonMessage)
                }
            })

        mockWebServer.enqueue(mockResponse)

        viewModel.connectToRoomList()

        delay(100)

        assertEquals("Chat rooms not arrived", testChatRooms, viewModel.UserRoomList.value)
    }

    //message send/receive listener websocket test
    @Test
    fun testChatroomWS() = runBlocking {
        val mockResponse = MockResponse().withWebSocketUpgrade(object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {

                }
                override fun onMessage(webSocket: WebSocket, text: String) {
                    webSocket.send(text)
                }
            })
        mockWebServer.enqueue(mockResponse)
        viewModel._currentUser.value=user
        viewModel._currentRoomId.value=1
        viewModel.connectToChatRoom( viewModel._currentRoomId.value)
        viewModel.sendWebSocketMessage(messageTest.text)
        delay(100)
        assertEquals("Chat message not arrived",messageTest.text , viewModel.messages.value.last().text )
    }
}