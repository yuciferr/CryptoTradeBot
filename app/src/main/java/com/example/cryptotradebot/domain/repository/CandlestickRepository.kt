package com.example.cryptotradebot.domain.repository

import com.example.cryptotradebot.domain.model.Candlestick
import com.example.cryptotradebot.utils.Resource

interface CandlestickRepository {
    suspend fun getCandlesticks(
        symbol: String,
        interval: String,
        startTime: Long? = null,
        endTime: Long? = null,
        limit: Int? = null
    ): Resource<List<Candlestick>>
} 