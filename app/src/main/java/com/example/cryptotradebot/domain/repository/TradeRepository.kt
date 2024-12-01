package com.example.cryptotradebot.domain.repository

import com.example.cryptotradebot.data.remote.dto.BacktestRequest
import kotlinx.coroutines.flow.Flow

interface TradeRepository {
    suspend fun runBacktest(request: BacktestRequest): Result<Map<String, Any>>
    
    suspend fun startLiveTrade(request: Map<String, Any>): Result<Map<String, Any>>
    suspend fun getLiveTradeStatus(): Result<Map<String, Any>>
    suspend fun stopLiveTrade(): Result<Map<String, Any>>
    
    fun connectToTradeUpdates()
    fun disconnectFromTradeUpdates()
    fun getTradeUpdates(): Flow<String>
    fun sendTradeMessage(message: String)
} 