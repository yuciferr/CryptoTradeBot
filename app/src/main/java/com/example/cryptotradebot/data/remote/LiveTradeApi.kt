package com.example.cryptotradebot.data.remote

import CloseSessionResponse
import SessionResponse
import TradeRequest
import TradeResponse
import retrofit2.Response
import retrofit2.http.*

interface LiveTradeApi {
    @POST("/api/livetrade/")
    suspend fun startLiveTrade(@Body request: TradeRequest): Response<TradeResponse>

    @GET("/api/livetrade/")
    suspend fun getLiveTradeStatus(@Query("symbol") symbol: String? = null): Response<List<SessionResponse>>

    @DELETE("/api/livetrade/")
    suspend fun stopLiveTrade(@Query("symbol") symbol: String? = null): Response<CloseSessionResponse>
} 