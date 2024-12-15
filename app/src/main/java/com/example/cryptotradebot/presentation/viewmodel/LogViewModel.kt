package com.example.cryptotradebot.presentation.viewmodel

import TradeRequest
import TradeResponse
import TradeStatus
import WebSocketSignal
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptotradebot.R
import com.example.cryptotradebot.data.remote.TradeWebSocketService
import com.example.cryptotradebot.data.remote.dto.request.BacktestRequest
import com.example.cryptotradebot.data.remote.dto.request.BollingerSettings
import com.example.cryptotradebot.data.remote.dto.request.EMASettings
import com.example.cryptotradebot.data.remote.dto.request.IndicatorSettings
import com.example.cryptotradebot.data.remote.dto.request.MACDSettings
import com.example.cryptotradebot.data.remote.dto.request.RSISettings
import com.example.cryptotradebot.data.remote.dto.request.RiskManagement
import com.example.cryptotradebot.data.remote.dto.request.SMASettings
import com.example.cryptotradebot.data.remote.dto.request.SupertrendSettings
import com.example.cryptotradebot.domain.model.Candlestick
import com.example.cryptotradebot.domain.model.Strategy
import com.example.cryptotradebot.domain.model.TradeLog
import com.example.cryptotradebot.domain.model.TradeType
import com.example.cryptotradebot.domain.use_case.GetCandlesticksUseCase
import com.example.cryptotradebot.domain.use_case.trade.ConnectToTradeSignalsUseCase
import com.example.cryptotradebot.domain.use_case.trade.DisconnectFromTradeSignalsUseCase
import com.example.cryptotradebot.domain.use_case.trade.GetLiveTradeStatusUseCase
import com.example.cryptotradebot.domain.use_case.trade.GetTradeSignalsUseCase
import com.example.cryptotradebot.domain.use_case.trade.RunBacktestUseCase
import com.example.cryptotradebot.domain.use_case.trade.StartLiveTradeUseCase
import com.example.cryptotradebot.domain.use_case.trade.StopLiveTradeUseCase
import com.example.cryptotradebot.utils.Resource
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

sealed class LogUiState {
    object Loading : LogUiState()
    data class Error(val message: String) : LogUiState()
    data class Success(val data: Any) : LogUiState()
}

@HiltViewModel
class LogViewModel @Inject constructor(
    private val getCandlesticksUseCase: GetCandlesticksUseCase,
    private val runBacktestUseCase: RunBacktestUseCase,
    private val startLiveTradeUseCase: StartLiveTradeUseCase,
    private val getLiveTradeStatusUseCase: GetLiveTradeStatusUseCase,
    private val stopLiveTradeUseCase: StopLiveTradeUseCase,
    private val connectToTradeSignalsUseCase: ConnectToTradeSignalsUseCase,
    private val disconnectFromTradeSignalsUseCase: DisconnectFromTradeSignalsUseCase,
    private val getTradeSignalsUseCase: GetTradeSignalsUseCase,
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val tradeWebSocketService: TradeWebSocketService
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
    private var isRetrying = false
    
    private var _selectedCoin = MutableStateFlow("BTC")
    val selectedCoin = _selectedCoin.asStateFlow()
    
    private var _selectedInterval = MutableStateFlow("1h")
    val selectedInterval = _selectedInterval.asStateFlow()

    private val _uiState = MutableStateFlow<LogUiState>(LogUiState.Success(Unit))
    val uiState = _uiState.asStateFlow()

    private var lastRequest: BacktestRequest? = null

    private val _liveTradeState = MutableStateFlow<LiveTradeState>(LiveTradeState.Idle)
    val liveTradeState = _liveTradeState.asStateFlow()

    private val _tradeSignals = mutableStateOf<List<WebSocketSignal>>(emptyList())
    val tradeSignals: State<List<WebSocketSignal>> = _tradeSignals

    private var liveTradeJob: Job? = null
    private var signalCollectionJob: Job? = null

    private var updateJob: Job? = null

    private val _logs = MutableStateFlow<List<WebSocketSignal>>(emptyList())
    val logs = _logs.asStateFlow()

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

        getCandlesticks()

        viewModelScope.launch {
            tradeWebSocketService.tradeSignals.collect { signal ->
                _logs.update { currentLogs ->
                    (currentLogs + signal).sortedBy { it.message.signal.timestamp }
                }
            }
        }
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
            if (!isRetrying) {
                backtestJob?.cancel()
            }
            backtestJob = viewModelScope.launch {
                try {
                    if (!isRetrying) {
                        _isBacktestRunning.value = true
                        _backtestResults.value = emptyList()
                        _uiState.value = LogUiState.Loading
                    }
                    
                    val request = if (!isRetrying) {
                        BacktestRequest(
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
                        ).also { lastRequest = it }
                    } else {
                        lastRequest ?: throw IllegalStateException("No last request found")
                    }

                    when (val result = runBacktestUseCase(request)) {
                        is Resource.Success -> {
                            isRetrying = false
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
                            if (!isRetrying && (result.message?.contains("timeout", ignoreCase = true) == true || 
                                result.message?.contains("standaloneCoroutine was cancelled", ignoreCase = true) == true)) {
                                _uiState.value = LogUiState.Loading
                                isRetrying = true
                                delay(1000)
                                startBacktest()
                            } else {
                                isRetrying = false
                                _uiState.value = LogUiState.Error(
                                    result.message ?: context.getString(R.string.error_backtest_failed)
                                )
                            }
                        }
                        is Resource.Loading -> {
                            _uiState.value = LogUiState.Loading
                        }
                    }
                } catch (e: Exception) {
                    if (!isRetrying && (e.message?.contains("timeout", ignoreCase = true) == true || 
                        e.message?.contains("standaloneCoroutine was cancelled", ignoreCase = true) == true)) {
                        _uiState.value = LogUiState.Loading
                        isRetrying = true
                        delay(1000)
                        startBacktest()
                    } else {
                        isRetrying = false
                        _uiState.value = LogUiState.Error(
                            "Backtest işlemi sırasında bir hata oluştu: ${e.message}"
                        )
                    }
                } finally {
                    if (!isRetrying) {
                        _isBacktestRunning.value = false
                    }
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
                    overbought = indicator.triggerCondition.compareValue?.toInt() ?: 70,
                    oversold = indicator.triggerCondition.value.toInt()
                )
            },
            macd = strategy.indicators.find { it.name == "MACD" }?.let { indicator ->
                MACDSettings(
                    fast = indicator.parameters.find { it.name == "Fast Period" }?.value?.toInt() ?: 12,
                    slow = indicator.parameters.find { it.name == "Slow Period" }?.value?.toInt() ?: 26,
                    signal = indicator.parameters.find { it.name == "Signal Period" }?.value?.toInt() ?: 9
                )
            },
            bollinger = strategy.indicators.find { it.name == "Bollinger Bands" }?.let { indicator ->
                BollingerSettings(
                    period = indicator.parameters.find { it.name == "Period" }?.value?.toInt() ?: 20,
                    std = indicator.parameters.find { it.name == "Upper Deviation" }?.value?.toDouble() ?: 2.0,
                    windowDev = indicator.parameters.find { it.name == "Lower Deviation" }?.value?.toDouble() ?: 2.0,
                    movingAvg = "sma"
                )
            },
            supertrend = strategy.indicators.find { it.name == "SuperTrend" }?.let { indicator ->
                SupertrendSettings(
                    period = indicator.parameters.find { it.name == "Period" }?.value?.toInt() ?: 7,
                    multiplier = indicator.parameters.find { it.name == "Multiplier" }?.value?.toDouble() ?: 0.8
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
        isRetrying = false
        backtestJob?.cancel()
        _isBacktestRunning.value = false
    }

    fun startLiveTrading() {
        strategy.value?.let { currentStrategy ->
            viewModelScope.launch {
                try {
                    Log.d(TAG, "Canlı trade başlatılıyor...")
                    val request = TradeRequest(
                        symbol = "${selectedCoin.value}USDT",
                        initial_balance = currentStrategy.tradeAmount?.toDouble() ?: 10000.0,
                        indicator_settings = createIndicatorSettings(currentStrategy),
                        risk_management = RiskManagement(
                            stopLoss = currentStrategy.stopLossPercentage?.toDouble() ?: 1.5,
                            takeProfit = currentStrategy.takeProfitPercentage?.toDouble() ?: 2.0
                        )
                    )
                    Log.d(TAG, "Trade isteği oluşturuldu: $request")
                    
                    _liveTradeState.value = LiveTradeState.Loading

                    when (val result = startLiveTradeUseCase(request)) {
                        is Resource.Success -> {
                            Log.d(TAG, "Canlı trade başarıyla başlatıldı")
                            result.data?.let { response ->
                                _liveTradeState.value = LiveTradeState.Running(response)
                                connectToTradeSignals()
                                startCollectingSignals()
                                startPeriodicUpdates()
                            } ?: run {
                                _liveTradeState.value = LiveTradeState.Error(
                                    context.getString(R.string.error_live_trade_no_response)
                                )
                            }
                        }
                        is Resource.Error -> {
                            Log.e(TAG, "Canlı trade başlatma hatası: ${result.message}")
                            _liveTradeState.value = LiveTradeState.Error(
                                result.message ?: context.getString(R.string.error_live_trade_failed)
                            )
                        }
                        is Resource.Loading -> {
                            _liveTradeState.value = LiveTradeState.Loading
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Canlı trade başlatma exception: ", e)
                    _liveTradeState.value = LiveTradeState.Error(
                        "Live trade başlatılırken hata oluştu: ${e.message}"
                    )
                }
            }
        } ?: run {
            _liveTradeState.value = LiveTradeState.Error(context.getString(R.string.error_no_strategy))
        }
    }

    private fun startPeriodicUpdates() {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            while (true) {
                delay(60000)
                Log.d(TAG, "Periyodik güncelleme başlatılıyor...")
                
                getCandlesticks()
                
                when (val result = getLiveTradeStatusUseCase()) {
                    is Resource.Success -> {
                        result.data?.firstOrNull()?.let { sessionResponse ->
                            _liveTradeState.value = LiveTradeState.Running(
                                TradeResponse(
                                    message = "LiveTrade güncellendi",
                                    session_id = sessionResponse.session.id,
                                    status = TradeStatus(
                                        is_running = true,
                                        balance = sessionResponse.session.current_balance,
                                        profit_loss = sessionResponse.session.current_balance - sessionResponse.session.initial_balance,
                                        profit_loss_percentage = ((sessionResponse.session.current_balance - sessionResponse.session.initial_balance) / sessionResponse.session.initial_balance) * 100,
                                        total_trades = sessionResponse.session.total_trades,
                                        winning_trades = sessionResponse.session.winning_trades,
                                        losing_trades = sessionResponse.session.losing_trades,
                                        current_position = null,
                                        last_price = 0.0,
                                        last_update = sessionResponse.session.last_update
                                    ),
                                    session = sessionResponse.session,
                                    signals = sessionResponse.signals
                                )
                            )
                        }
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "Durum güncellenirken hata: ${result.message}")
                    }
                    is Resource.Loading -> {
                        // Loading durumunu gösterme
                    }
                }
            }
        }
    }

    fun stopLiveTrading() {
        viewModelScope.launch {
            try {
                updateJob?.cancel()
                _liveTradeState.value = LiveTradeState.Loading
                
                when (val result = stopLiveTradeUseCase()) {
                    is Resource.Success -> {
                        disconnectFromTradeSignals()
                        _liveTradeState.value = LiveTradeState.Idle
                        _tradeSignals.value = emptyList()
                    }
                    is Resource.Error -> {
                        _liveTradeState.value = LiveTradeState.Error(
                            result.message ?: context.getString(R.string.error_stop_live_trade_failed)
                        )
                    }
                    is Resource.Loading -> {
                        _liveTradeState.value = LiveTradeState.Loading
                    }
                }
            } catch (e: Exception) {
                _liveTradeState.value = LiveTradeState.Error(
                    "Live trade durdurulurken hata oluştu: ${e.message}"
                )
            }
        }
    }

    private fun connectToTradeSignals() {
        Log.d(TAG, "WebSocket bağlantısı başlatılıyor")
        connectToTradeSignalsUseCase()
    }

    private fun disconnectFromTradeSignals() {
        Log.d(TAG, "WebSocket bağlantısı kapatılıyor")
        disconnectFromTradeSignalsUseCase()
        signalCollectionJob?.cancel()
    }

    private fun startCollectingSignals() {
        signalCollectionJob?.cancel()
        signalCollectionJob = viewModelScope.launch {
            Log.d(TAG, "Sinyal toplama başlatıldı")
            getTradeSignalsUseCase().collect { signal ->
                Log.d(TAG, "Yeni sinyal alındı: $signal")
                _tradeSignals.value = _tradeSignals.value + signal
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = null
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        updateJob?.cancel()
        disconnectFromTradeSignals()
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

    sealed class LiveTradeState {
        object Idle : LiveTradeState()
        object Loading : LiveTradeState()
        data class Running(val tradeResponse: TradeResponse) : LiveTradeState()
        data class Error(val message: String) : LiveTradeState()
    }

    companion object {
        private const val TAG = "CryptoTradeBot/Log"
    }
} 