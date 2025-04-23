Mobile android client for end‑to‑end chat part of Ktor chat service made by me as a learning pet-project.
(WebSocket+REST)
Jetpack Compose UI. 
Kotlin

> The project is part of a full‑stack showcase (server + client).  
> The backend part: [`chat‑server`](https://github.com/Ionnek/Chat_Server_Ktor_CIO) repo.

Features:
Real-time: WebSocket channel per room, message broadcast,
Security: JWT login / registration, token interceptor
UI: Jetpack Compose, Material 3
Architecture: MVVM, stateflow
Tests: integration tests (MockWebServer + WS)

Tech stack:
Jetpack Compose, Material 3, Navigation‑Compose,
Hilt,Retrofit 2, OkHttp WebSocket, Kotlin Serialization,
MockWebServer, Ktor‑client test,Coroutines, StateFlow

RUN:
Requirements
* Android Studio Jellyfish / Flamingo+
* JDK 17
* Android SDK API 24→34
* A running instance of the
  [`chat‑server`](https://github.com/Ionnek/Chat_Server_Ktor_CIO) (Docker‑compose provided)

Guide to tests
* open this project in the android studio
* start IntegrationsTests in the test package

Guide to start
* set up server [`chat‑server`](https://github.com/Ionnek/Chat_Server_Ktor_CIO)
* open this project in the android studio
* find the NetworkModule in the DI.kt file
* replace localhost with [`chat‑server`] active link
* build the project

