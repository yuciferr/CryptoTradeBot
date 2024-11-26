package com.example.cryptotradebot.data.repository

import com.example.cryptotradebot.data.remote.BinanceApi
import com.example.cryptotradebot.domain.model.Candlestick
import com.example.cryptotradebot.domain.repository.CandlestickRepository
import com.example.cryptotradebot.utils.Resource
import javax.inject.Inject

class CandlestickRepositoryImpl @Inject constructor(
    private val api: BinanceApi
) : CandlestickRepository {

    override suspend fun getCandlesticks(
        symbol: String,
        interval: String,
        startTime: Long?,
        endTime: Long?,
        limit: Int?
    ): Resource<List<Candlestick>> {
        return try {
            val response = api.getCandlesticks(symbol, interval, startTime, endTime, limit)
            Resource.Success(
                response.map { item ->
                    Candlestick(
                        openTime = item[0].toLong(),
                        open = item[1],
                        high = item[2],
                        low = item[3],
                        close = item[4],
                        volume = item[5],
                        closeTime = item[6].toLong(),
                        quoteAssetVolume = item[7],
                        numberOfTrades = item[8].toInt(),
                        takerBuyBaseAssetVolume = item[9],
                        takerBuyQuoteAssetVolume = item[10]
                    )
                }
            )
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Beklenmeyen bir hata oluştu.")
        }
    }
} 