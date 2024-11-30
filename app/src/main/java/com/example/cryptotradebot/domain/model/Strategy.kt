package com.example.cryptotradebot.domain.model

data class Strategy(
    val id: String,
    val name: String,
    val coin: String,
    val timeframe: String,
    val indicators: List<Indicator>,
    val isActive: Boolean,
    val createdAt: Long,
    val takeProfitPercentage: Float? = null,
    val stopLossPercentage: Float? = null,
    val tradeAmount: Float? = null
)