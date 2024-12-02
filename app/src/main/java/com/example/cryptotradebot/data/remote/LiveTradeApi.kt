package com.example.cryptotradebot.data.remote

import retrofit2.Response
import retrofit2.http.*

interface LiveTradeApi {
    @POST("/api/livetrade/")
    suspend fun startLiveTrade(@Body request: Map<String, Any>): Response<Map<String, Any>>

    @GET("/api/livetrade/")
    suspend fun getLiveTradeStatus(@Query("symbol") symbol: String? = null): Response<List<Map<String, Any>>>

    @DELETE("/api/livetrade/")
    suspend fun stopLiveTrade(@Query("symbol") symbol: String? = null): Response<Map<String, Any>>
} 