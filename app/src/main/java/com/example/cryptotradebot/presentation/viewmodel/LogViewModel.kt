package com.example.cryptotradebot.presentation.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptotradebot.R
import com.example.cryptotradebot.data.remote.dto.BacktestRequest
import com.example.cryptotradebot.data.remote.dto.BollingerSettings
import com.example.cryptotradebot.data.remote.dto.EMASettings
import com.example.cryptotradebot.data.remote.dto.IndicatorSettings
import com.example.cryptotradebot.data.remote.dto.MACDSettings
import com.example.cryptotradebot.data.remote.dto.RSISettings
import com.example.cryptotradebot.data.remote.dto.RiskManagement
import com.example.cryptotradebot.data.remote.dto.SMASettings
import com.example.cryptotradebot.domain.model.TradeLog
import com.example.cryptotradebot.domain.model.TradeType
import com.example.cryptotradebot.domain.model.Candlestick
import com.example.cryptotradebot.domain.model.Strategy
import com.example.cryptotradebot.domain.model.TriggerType
import com.example.cryptotradebot.domain.use_case.GetCandlesticksUseCase
import com.example.cryptotradebot.domain.use_case.trade.RunBacktestUseCase
import com.example.cryptotradebot.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.random.Random
import com.google.gson.Gson
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import java.text.SimpleDateFormat

sealed class LogUiState {
    object Loading : LogUiState()
    data class Error(val message: String) : LogUiState()
    data class Success(val data: Any) : LogUiState()
}

@HiltViewModel
class LogViewModel @Inject constructor(
    private val getCandlesticksUseCase: GetCandlesticksUseCase,
    private val runBacktestUseCase: RunBacktestUseCase,
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(LogState())
    val state: State<LogState> = _state

    private val _candlesticks = MutableStateFlow<List<Candlestick>>(emptyList())
    val candlesticks = _candlesticks.asStateFlow()

    private var _strategy = MutableStateFlow<Strategy?>(null)
    val strategy = _strategy.asStateFlow()

    private var _isBacktestRunning = MutableStateFlow(false)
    val isBacktestRunning = _isBacktestRunning.asStateFlow()

    private var _backtestResults = MutableStateFlow<List<TradeLog>>(emptyList())
    val backtestResults = _backtestResults.asStateFlow()

    private var fetchJob: Job? = null
    private var backtestJob: Job? = null
    
    private var _selectedCoin = MutableStateFlow("BTC")
    val selectedCoin = _selectedCoin.asStateFlow()
    
    private var _selectedInterval = MutableStateFlow("1h")
    val selectedInterval = _selectedInterval.asStateFlow()

    private val _uiState = MutableStateFlow<LogUiState>(LogUiState.Success(Unit))
    val uiState = _uiState.asStateFlow()

    private var lastRequest: BacktestRequest? = null

    init {
        savedStateHandle.get<String>("selectedStrategyId")?.let { strategyId ->
            _state.value = _state.value.copy(selectedStrategyId = strategyId)
            android.util.Log.d("yuci", "LogViewModel - SelectedStrategyId: $strategyId")
        }
        savedStateHandle.get<Boolean>("showBacktestOnly")?.let { showBacktest ->
            _state.value = _state.value.copy(showBacktestOnly = showBacktest)
            android.util.Log.d("yuci", "LogViewModel - ShowBacktestOnly: $showBacktest")
        }
        savedStateHandle.get<String>("strategyJson")?.let { strategyJson ->
            android.util.Log.d("yuci", "LogViewModel - Received StrategyJson: $strategyJson")
            try {
                val gson = Gson()
                val strategy = gson.fromJson(strategyJson, Strategy::class.java)
                android.util.Log.d("yuci", "LogViewModel - Parsed Strategy: $strategy")
                _strategy.value = strategy
                _selectedCoin.value = strategy.coin
                _selectedInterval.value = strategy.timeframe
                getCandlesticks()
            } catch (e: Exception) {
                android.util.Log.e("yuci", "LogViewModel - Strategy Parse Error: ${e.message}", e)
                _state.value = _state.value.copy(
                    error = context.getString(R.string.error_strategy_parse)
                )
            }
        } ?: run {
            android.util.Log.d("yuci", "LogViewModel - No StrategyJson received")
        }
        loadMockData()
        getCandlesticks()
    }

    private fun getCandlesticks() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            when (val result = getCandlesticksUseCase(
                symbol = "${_selectedCoin.value}USDT",
                interval = _selectedInterval.value,
                limit = 100
            )) {
                is Resource.Success -> {
                    result.data?.let { candlesticks ->
                        _candlesticks.value = candlesticks
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message ?: context.getString(R.string.error_unexpected)
                    )
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }
    }

    fun onCoinSelect(coin: String) {
        _selectedCoin.value = coin
        getCandlesticks()
    }

    fun onIntervalSelect(interval: String) {
        _selectedInterval.value = interval
        getCandlesticks()
    }

    fun refreshData() {
        getCandlesticks()
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

    fun updateStrategy(strategyJson: String) {
        try {
            val gson = Gson()
            val strategy = gson.fromJson(strategyJson, Strategy::class.java)
            _strategy.value = strategy
            _selectedCoin.value = strategy.coin
            _selectedInterval.value = strategy.timeframe
            _state.value = _state.value.copy(error = null)
            getCandlesticks()
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                error = context.getString(R.string.error_strategy_parse)
            )
        }
    }

    fun startBacktest() {
        _strategy.value?.let { strategy ->
            backtestJob?.cancel()
            backtestJob = viewModelScope.launch {
                _isBacktestRunning.value = true
                _backtestResults.value = emptyList()
                _uiState.value = LogUiState.Loading
                
                try {
                    val request = BacktestRequest(
                        symbol = "${strategy.coin}USDT",
                        timeframe = strategy.timeframe,
                        initialBalance = strategy.tradeAmount?.toDouble() ?: 10000.0,
                        indicatorSettings = createIndicatorSettings(strategy),
                        riskManagement = if (strategy.takeProfitPercentage != null || strategy.stopLossPercentage != null) {
                            RiskManagement(
                                stopLoss = strategy.stopLossPercentage?.toDouble() ?: 1.5,
                                takeProfit = strategy.takeProfitPercentage?.toDouble() ?: 2.0
                            )
                        } else null
                    )
                    lastRequest = request

                    when (val result = runBacktestUseCase(request)) {
                        is Resource.Success -> {
                            result.data?.let { response ->
                                val tradeLogs = response.trades.map { trade ->
                                    TradeLog(
                                        id = UUID.randomUUID().toString(),
                                        strategyId = strategy.id,
                                        strategyName = strategy.name,
                                        coin = strategy.coin,
                                        type = if (trade.profitLoss >= 0) TradeType.SELL else TradeType.BUY,
                                        price = trade.exitPrice,
                                        amount = response.summary.initialBalance / trade.entryPrice,
                                        total = trade.exitPrice * (response.summary.initialBalance / trade.entryPrice),
                                        timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                                            .parse(trade.exitTime)?.time ?: System.currentTimeMillis(),
                                        profit = trade.profitPercentage,
                                        isBacktest = true
                                    )
                                }
                                _backtestResults.value = tradeLogs
                                _uiState.value = LogUiState.Success(response.summary)
                            }
                        }
                        is Resource.Error -> {
                            _uiState.value = LogUiState.Error(
                                result.message ?: context.getString(R.string.error_backtest_failed)
                            )
                        }
                        is Resource.Loading -> {
                            _uiState.value = LogUiState.Loading
                        }
                    }
                } catch (e: Exception) {
                    _uiState.value = LogUiState.Error(
                        "Backtest işlemi sırasında bir hata oluştu: ${e.message}"
                    )
                } finally {
                    _isBacktestRunning.value = false
                }
            }
        } ?: run {
            _uiState.value = LogUiState.Error(context.getString(R.string.error_no_strategy))
        }
    }

    private fun createIndicatorSettings(strategy: Strategy): IndicatorSettings {
        return IndicatorSettings(
            rsi = strategy.indicators.find { it.name == "RSI" }?.let { indicator ->
                RSISettings(
                    period = indicator.parameters.find { it.name == "Period" }?.value?.toInt() ?: 14,
                    overbought = 70,
                    oversold = 30
                )
            },
            macd = strategy.indicators.find { it.name == "MACD" }?.let { indicator ->
                MACDSettings(
                    fast = indicator.parameters.find { it.name == "Fast Period" }?.value?.toInt() ?: 12,
                    slow = indicator.parameters.find { it.name == "Slow Period" }?.value?.toInt() ?: 26,
                    signal = indicator.parameters.find { it.name == "Signal Period" }?.value?.toInt() ?: 9
                )
            },
            bollinger = strategy.indicators.find { it.name == "Bollinger" }?.let { indicator ->
                BollingerSettings(
                    period = indicator.parameters.find { it.name == "Period" }?.value?.toInt() ?: 20,
                    std = indicator.parameters.find { it.name == "Standard Deviation" }?.value?.toInt() ?: 2
                )
            },
            sma = strategy.indicators.find { it.name == "SMA" }?.let { indicator ->
                SMASettings(
                    periods = listOf(indicator.parameters.find { it.name == "Period" }?.value?.toInt() ?: 20)
                )
            },
            ema = strategy.indicators.find { it.name == "EMA" }?.let { indicator ->
                EMASettings(
                    periods = listOf(indicator.parameters.find { it.name == "Period" }?.value?.toInt() ?: 20)
                )
            }
        )
    }

    fun retryLastRequest() {
        lastRequest?.let {
            startBacktest()
        }
    }

    fun stopBacktest() {
        backtestJob?.cancel()
        _isBacktestRunning.value = false
    }

    data class LogState(
        val logs: List<TradeLog> = emptyList(),
        val showBacktestOnly: Boolean = false,
        val selectedStrategyId: String? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    data class StrategyStats(
        val strategyName: String,
        val totalTrades: Int,
        val successfulTrades: Int,
        val successRate: Float,
        val averageProfit: Double
    )

    fun getStrategyStats(state: LogState): StrategyStats {
        val strategyLogs = state.logs.filter { 
            it.strategyId == state.selectedStrategyId && it.isBacktest == state.showBacktestOnly
        }
        
        val totalTrades = strategyLogs.size
        val successfulTrades = strategyLogs.count { it.profit != null && it.profit > 0 }
        val successRate = if (totalTrades > 0) (successfulTrades.toFloat() / totalTrades) * 100 else 0f
        val averageProfit = strategyLogs
            .mapNotNull { it.profit }
            .takeIf { it.isNotEmpty() }
            ?.average() ?: 0.0

        return StrategyStats(
            strategyName = strategyLogs.firstOrNull()?.strategyName ?: context.getString(R.string.log_strategy_unknown),
            totalTrades = totalTrades,
            successfulTrades = successfulTrades,
            successRate = successRate,
            averageProfit = averageProfit
        )
    }
} 