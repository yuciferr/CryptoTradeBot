package com.example.cryptotradebot.domain.use_case

import com.example.cryptotradebot.domain.model.Candlestick
import com.example.cryptotradebot.domain.repository.CandlestickRepository
import com.example.cryptotradebot.utils.Resource
import javax.inject.Inject

class GetCandlesticksUseCase @Inject constructor(
    private val repository: CandlestickRepository
) {
    suspend operator fun invoke(
        symbol: String,
        interval: String,
        startTime: Long? = null,
        endTime: Long? = null,
        limit: Int? = null
    ): Resource<List<Candlestick>> {
        return repository.getCandlesticks(symbol, interval, startTime, endTime, limit)
    }
} 