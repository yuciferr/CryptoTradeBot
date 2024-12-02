package com.example.cryptotradebot.data.remote

import android.util.Log
import com.example.cryptotradebot.domain.model.SignalType
import com.example.cryptotradebot.domain.model.TradeSignal
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
    private val _tradeSignals = MutableSharedFlow<TradeSignal>()
    val tradeSignals: Flow<TradeSignal> = _tradeSignals

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
                    val signalResponse = gson.fromJson(text, SignalResponse::class.java)
                    if (signalResponse.type == "trade_signal") {
                        val signal = signalResponse.signal
                        val tradeSignal = TradeSignal(
                            symbol = signal.symbol,
                            signalType = SignalType.valueOf(signal.signal_type),
                            timestamp = signal.timestamp,
                            price = signal.price
                        )
                        _tradeSignals.tryEmit(tradeSignal)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing WebSocket message", e)
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

    private data class SignalResponse(
        val type: String,
        val signal: Signal
    )

    private data class Signal(
        val symbol: String,
        val signal_type: String,
        val timestamp: String,
        val price: Double
    )

    companion object {
        private const val TAG = "TradeWebSocketService"
    }
} 