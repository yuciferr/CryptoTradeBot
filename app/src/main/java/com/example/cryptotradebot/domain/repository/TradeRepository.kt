package com.example.cryptotradebot.domain.repository

import CloseSessionResponse
import SessionResponse
import TradeRequest
import TradeResponse
import WebSocketSignal
import com.example.cryptotradebot.data.remote.dto.request.BacktestRequest
import com.example.cryptotradebot.data.remote.dto.response.BacktestResponse
import kotlinx.coroutines.flow.Flow

interface TradeRepository {
    suspend fun runBacktest(request: BacktestRequest): Result<BacktestResponse>
    
    suspend fun startLiveTrade(request: TradeRequest): Result<TradeResponse>
    suspend fun getLiveTradeStatus(symbol: String? = null): Result<List<SessionResponse>>
    suspend fun stopLiveTrade(symbol: String? = null): Result<CloseSessionResponse>
    
    fun connectToTradeSignals()
    fun disconnectFromTradeSignals()
    fun getTradeSignals(): Flow<WebSocketSignal>
} 