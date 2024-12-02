package com.example.cryptotradebot.domain.model

data class TradeSignal(
    val symbol: String,
    val signalType: SignalType,
    val timestamp: String,
    val price: Double
)

enum class SignalType {
    BUY, SELL
} 