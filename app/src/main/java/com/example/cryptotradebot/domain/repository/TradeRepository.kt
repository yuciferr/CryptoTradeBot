package com.example.cryptotradebot.domain.repository

import com.example.cryptotradebot.data.remote.dto.BacktestRequest
import com.example.cryptotradebot.domain.model.TradeSignal
import kotlinx.coroutines.flow.Flow

interface TradeRepository {
    suspend fun runBacktest(request: BacktestRequest): Result<Map<String, Any>>
    
    suspend fun startLiveTrade(request: Map<String, Any>): Result<Map<String, Any>>
    suspend fun getLiveTradeStatus(symbol: String? = null): Result<List<Map<String, Any>>>
    suspend fun stopLiveTrade(symbol: String? = null): Result<Map<String, Any>>
    
    fun connectToTradeSignals()
    fun disconnectFromTradeSignals()
    fun getTradeSignals(): Flow<TradeSignal>
} 