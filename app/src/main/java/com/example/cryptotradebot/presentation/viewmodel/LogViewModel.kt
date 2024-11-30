package com.example.cryptotradebot.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptotradebot.domain.model.TradeLog
import com.example.cryptotradebot.domain.model.TradeType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class LogViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(LogState())
    val state: State<LogState> = _state

    init {
        savedStateHandle.get<String>("selectedStrategyId")?.let { strategyId ->
            _state.value = _state.value.copy(selectedStrategyId = strategyId)
        }
        savedStateHandle.get<Boolean>("showBacktestOnly")?.let { showBacktest ->
            _state.value = _state.value.copy(showBacktestOnly = showBacktest)
        }
        loadMockData()
    }

    private fun loadMockData() {
        val mockLogs = mutableListOf<TradeLog>()
        val coins = listOf("BTC", "ETH", "SOL", "AVAX")
        val strategies = listOf(
            "Golden Cross", "RSI Divergence", "MACD Cross", "Bollinger Bounce"
        )

        // Son 24 saat için mock veriler
        val currentTime = System.currentTimeMillis()
        val oneDayAgo = currentTime - (24 * 60 * 60 * 1000)

        repeat(20) { index ->
            val timestamp = Random.nextLong(oneDayAgo, currentTime)
            val coin = coins.random()
            val price = when (coin) {
                "BTC" -> Random.nextDouble(40000.0, 45000.0)
                "ETH" -> Random.nextDouble(2000.0, 2500.0)
                "SOL" -> Random.nextDouble(80.0, 100.0)
                else -> Random.nextDouble(20.0, 30.0)
            }
            val amount = Random.nextDouble(0.1, 1.0)
            val type = if (index % 2 == 0) TradeType.BUY else TradeType.SELL
            val profit = if (type == TradeType.SELL) Random.nextDouble(-5.0, 15.0) else null

            mockLogs.add(
                TradeLog(
                    id = UUID.randomUUID().toString(),
                    strategyId = "strategy_${index % 4}",
                    strategyName = strategies[index % 4],
                    coin = coin,
                    type = type,
                    price = price,
                    amount = amount,
                    total = price * amount,
                    timestamp = timestamp,
                    isBacktest = Random.nextBoolean(),
                    profit = profit
                )
            )
        }

        _state.value = _state.value.copy(
            logs = mockLogs.sortedByDescending { it.timestamp }
        )
    }

    fun filterLogs(showBacktest: Boolean) {
        _state.value = _state.value.copy(
            showBacktestOnly = showBacktest,
            selectedStrategyId = null
        )
    }

    fun clearStrategyFilter() {
        _state.value = _state.value.copy(selectedStrategyId = null)
    }

    data class LogState(
        val logs: List<TradeLog> = emptyList(),
        val showBacktestOnly: Boolean = false,
        val selectedStrategyId: String? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    ) {
        fun getStrategyStats(): StrategyStats {
            val strategyLogs = logs.filter { 
                it.strategyId == selectedStrategyId && it.isBacktest == showBacktestOnly
            }
            
            val totalTrades = strategyLogs.size
            val successfulTrades = strategyLogs.count { it.profit != null && it.profit > 0 }
            val successRate = if (totalTrades > 0) (successfulTrades.toFloat() / totalTrades) * 100 else 0f
            val averageProfit = strategyLogs
                .mapNotNull { it.profit }
                .takeIf { it.isNotEmpty() }
                ?.average() ?: 0.0

            return StrategyStats(
                strategyName = strategyLogs.firstOrNull()?.strategyName ?: "Bilinmeyen Strateji",
                totalTrades = totalTrades,
                successfulTrades = successfulTrades,
                successRate = successRate,
                averageProfit = averageProfit
            )
        }
    }

    data class StrategyStats(
        val strategyName: String,
        val totalTrades: Int,
        val successfulTrades: Int,
        val successRate: Float,
        val averageProfit: Double
    )
} 