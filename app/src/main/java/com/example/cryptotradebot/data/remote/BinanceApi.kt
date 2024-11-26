package com.example.cryptotradebot.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface BinanceApi {
    @GET("/fapi/v1/klines")
    suspend fun getCandlesticks(
        @Query("symbol") symbol: String,
        @Query("interval") interval: String,
        @Query("startTime") startTime: Long?,
        @Query("endTime") endTime: Long?,
        @Query("limit") limit: Int?
    ): List<List<String>>
} 