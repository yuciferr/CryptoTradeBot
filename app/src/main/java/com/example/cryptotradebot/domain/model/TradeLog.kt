package com.example.cryptotradebot.domain.model

import java.util.Date

data class TradeLog(
    val id: String,
    val strategyId: String,
    val strategyName: String,
    val coin: String,
    val type: TradeType,
    val price: Double,
    val amount: Double,
    val total: Double,
    val timestamp: Long,
    val isBacktest: Boolean,
    val profit: Double? = null
)

enum class TradeType {
    BUY, SELL
} 