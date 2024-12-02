package com.example.cryptotradebot.data.repository

import android.util.Log
import com.example.cryptotradebot.data.remote.BacktestApi
import com.example.cryptotradebot.data.remote.LiveTradeApi
import com.example.cryptotradebot.data.remote.TradeWebSocketService
import com.example.cryptotradebot.data.remote.dto.BacktestRequest
import com.example.cryptotradebot.domain.model.TradeSignal
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
            Log.e(TAG, "Error running backtest", e)
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
            Log.e(TAG, "Error starting live trade", e)
            Result.failure(e)
        }
    }

    override suspend fun getLiveTradeStatus(symbol: String?): Result<List<Map<String, Any>>> {
        return try {
            val response = liveTradeApi.getLiveTradeStatus(symbol)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Get status failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting live trade status", e)
            Result.failure(e)
        }
    }

    override suspend fun stopLiveTrade(symbol: String?): Result<Map<String, Any>> {
        return try {
            val response = liveTradeApi.stopLiveTrade(symbol)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyMap())
            } else {
                Result.failure(Exception("Stop trade failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping live trade", e)
            Result.failure(e)
        }
    }

    override fun connectToTradeSignals() {
        webSocketService.connect()
    }

    override fun disconnectFromTradeSignals() {
        webSocketService.disconnect()
    }

    override fun getTradeSignals(): Flow<TradeSignal> {
        return webSocketService.tradeSignals
    }

    companion object {
        private const val TAG = "TradeRepositoryImpl"
    }
} 