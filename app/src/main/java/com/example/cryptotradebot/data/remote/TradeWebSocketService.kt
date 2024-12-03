package com.example.cryptotradebot.data.remote

import WebSocketSignal
import TradeSignal
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import okhttp3.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TradeWebSocketService @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    private var webSocket: WebSocket? = null
    private val gson = Gson()
    private val _tradeSignals = MutableSharedFlow<WebSocketSignal>()
    val tradeSignals: Flow<WebSocketSignal> = _tradeSignals

    fun connect() {
        val request = Request.Builder()
            .url("ws://localhost:8000/ws/trade_signals/")
            .build()

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket Connection opened")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val signal = gson.fromJson(text, WebSocketSignal::class.java)
                    _tradeSignals.tryEmit(signal)
                } catch (e: Exception) {
                    Log.e(TAG, "WebSocket mesajı işlenirken hata oluştu", e)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket Connection failure", t)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket Connection closing: $code - $reason")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket Connection closed: $code - $reason")
            }
        })
    }

    fun disconnect() {
        webSocket?.close(1000, "Disconnecting")
        webSocket = null
    }

    companion object {
        private const val TAG = "TradeWebSocketService"
    }
} 