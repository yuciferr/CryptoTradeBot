package com.example.cryptotradebot.data.remote

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TradeWebSocketService @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    private var webSocket: WebSocket? = null
    private val _tradeUpdates = MutableSharedFlow<String>()
    val tradeUpdates: Flow<String> = _tradeUpdates

    fun connect() {
        val request = Request.Builder()
            .url("ws://localhost:8000/ws/trade/")
            .build()

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                _tradeUpdates.tryEmit(text)
            }
        })
    }

    fun disconnect() {
        webSocket?.close(1000, "Disconnecting")
        webSocket = null
    }

    fun sendMessage(message: String) {
        webSocket?.send(message)
    }
} 