package com.example.cryptotradebot.data.repository

import CloseSessionResponse
import SessionResponse
import TradeRequest
import TradeResponse
import WebSocketSignal
import android.util.Log
import com.example.cryptotradebot.data.remote.BacktestApi
import com.example.cryptotradebot.data.remote.LiveTradeApi
import com.example.cryptotradebot.data.remote.TradeWebSocketService
import com.example.cryptotradebot.data.remote.dto.request.BacktestRequest
import com.example.cryptotradebot.data.remote.dto.response.BacktestResponse
import com.example.cryptotradebot.domain.repository.TradeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TradeRepositoryImpl @Inject constructor(
    private val backtestApi: BacktestApi,
    private val liveTradeApi: LiveTradeApi,
    private val webSocketService: TradeWebSocketService,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) : TradeRepository {

    override suspend fun runBacktest(request: BacktestRequest): Result<BacktestResponse> {
        return withContext(coroutineScope.coroutineContext) {
            try {
                val response = backtestApi.runBacktest(request)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Backtest yanıtı boş"))
                } else {
                    Result.failure(Exception("Backtest başarısız: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error running backtest", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun startLiveTrade(request: TradeRequest): Result<TradeResponse> {
        return withContext(coroutineScope.coroutineContext) {
            try {
                val response = liveTradeApi.startLiveTrade(request)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Live trade yanıtı boş"))
                } else {
                    Result.failure(Exception("Live trade başlatılamadı: ${response.code()}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Live trade başlatılırken hata oluştu", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun getLiveTradeStatus(symbol: String?): Result<List<SessionResponse>> {
        return withContext(coroutineScope.coroutineContext) {
            try {
                val response = liveTradeApi.getLiveTradeStatus(symbol)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Durum yanıtı boş"))
                } else {
                    Result.failure(Exception("Durum alınamadı: ${response.code()}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Durum alınırken hata oluştu", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun stopLiveTrade(symbol: String?): Result<CloseSessionResponse> {
        return withContext(coroutineScope.coroutineContext) {
            try {
                val response = liveTradeApi.stopLiveTrade(symbol)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Durdurma yanıtı boş"))
                } else {
                    Result.failure(Exception("Trade durdurulamadı: ${response.code()}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Trade durdurulurken hata oluştu", e)
                Result.failure(e)
            }
        }
    }

    override fun connectToTradeSignals() {
        webSocketService.connect()
    }

    override fun disconnectFromTradeSignals() {
        webSocketService.disconnect()
    }

    override fun getTradeSignals(): Flow<WebSocketSignal> {
        return webSocketService.tradeSignals
    }

    companion object {
        private const val TAG = "TradeRepositoryImpl"
    }
} 