package com.example.cryptotradebot.data.repository

import com.example.cryptotradebot.data.remote.BacktestApi
import com.example.cryptotradebot.data.remote.LiveTradeApi
import com.example.cryptotradebot.data.remote.TradeWebSocketService
import com.example.cryptotradebot.data.remote.dto.BacktestRequest
import com.example.cryptotradebot.domain.repository.TradeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TradeRepositoryImpl @Inject constructor(
    private val backtestApi: BacktestApi,
    private val liveTradeApi: LiveTradeApi,
    private val webSocketService: TradeWebSocketService
) : TradeRepository {

    override suspend fun runBacktest(request: BacktestRequest): Result<Map<String, Any>> {
        return try {
            val response = backtestApi.runBacktest(request)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyMap())
            } else {
                Result.failure(Exception("Backtest failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun startLiveTrade(request: Map<String, Any>): Result<Map<String, Any>> {
        return try {
            val response = liveTradeApi.startLiveTrade(request)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyMap())
            } else {
                Result.failure(Exception("Live trade start failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLiveTradeStatus(): Result<Map<String, Any>> {
        return try {
            val response = liveTradeApi.getLiveTradeStatus()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyMap())
            } else {
                Result.failure(Exception("Get status failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun stopLiveTrade(): Result<Map<String, Any>> {
        return try {
            val response = liveTradeApi.stopLiveTrade()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyMap())
            } else {
                Result.failure(Exception("Stop trade failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun connectToTradeUpdates() {
        webSocketService.connect()
    }

    override fun disconnectFromTradeUpdates() {
        webSocketService.disconnect()
    }

    override fun getTradeUpdates(): Flow<String> {
        return webSocketService.tradeUpdates
    }

    override fun sendTradeMessage(message: String) {
        webSocketService.sendMessage(message)
    }
} 