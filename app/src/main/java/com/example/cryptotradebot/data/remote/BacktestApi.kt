package com.example.cryptotradebot.data.remote

import com.example.cryptotradebot.data.remote.dto.request.BacktestRequest
import com.example.cryptotradebot.data.remote.dto.response.BacktestResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BacktestApi {
    @POST("/api/backtest/run/")
    suspend fun runBacktest(@Body request: BacktestRequest): Response<BacktestResponse>
} 