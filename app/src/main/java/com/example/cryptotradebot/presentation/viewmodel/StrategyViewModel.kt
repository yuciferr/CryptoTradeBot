package com.example.cryptotradebot.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptotradebot.domain.model.*
import com.example.cryptotradebot.domain.use_case.GetCandlesticksUseCase
import com.example.cryptotradebot.domain.use_case.strategy.*
import com.example.cryptotradebot.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StrategyViewModel @Inject constructor(
    private val getCandlesticksUseCase: GetCandlesticksUseCase,
    private val saveStrategyUseCase: SaveStrategyUseCase,
    private val updateStrategySettingsUseCase: UpdateStrategySettingsUseCase
) : ViewModel() {

    private val _state = mutableStateOf(StrategyState())
    val state: State<StrategyState> = _state

    private var priceUpdateJob: Job? = null
    private var currentStrategy: Strategy? = null

    init {
        startPriceUpdates()
    }

    private fun startPriceUpdates() {
        priceUpdateJob?.cancel()
        priceUpdateJob = viewModelScope.launch {
            while (true) {
                updatePrice()
                delay(5000)
            }
        }
    }

    private suspend fun updatePrice() {
        when (val result = getCandlesticksUseCase(
            symbol = "${state.value.selectedCoin}USDT",
            interval = "1m",
            limit = 1
        )) {
            is Resource.Success -> {
                result.data?.firstOrNull()?.let { candlestick ->
                    _state.value = _state.value.copy(
                        currentPrice = candlestick.close.toDouble(),
                        volume24h = candlestick.volume.toDouble(),
                        lastUpdateTime = System.currentTimeMillis(),
                        error = null
                    )
                }
            }
            is Resource.Error -> {
                _state.value = _state.value.copy(
                    error = result.message,
                    isLoading = false
                )
            }
            is Resource.Loading -> {
                _state.value = _state.value.copy(isLoading = true)
            }
        }
    }

    fun onCoinSelect(coin: String) {
        _state.value = _state.value.copy(selectedCoin = coin)
    }

    fun onTimeframeSelect(timeframe: String) {
        _state.value = _state.value.copy(selectedTimeframe = timeframe)
    }

    fun onAddIndicator(indicator: Indicator) {
        val currentIndicators = _state.value.selectedIndicators.toMutableList()
        currentIndicators.add(indicator)
        _state.value = _state.value.copy(selectedIndicators = currentIndicators)
    }

    fun onRemoveIndicator(indicator: Indicator) {
        val currentIndicators = _state.value.selectedIndicators.toMutableList()
        currentIndicators.remove(indicator)
        _state.value = _state.value.copy(selectedIndicators = currentIndicators)
    }

    fun onUpdateIndicator(index: Int, indicator: Indicator) {
        val currentIndicators = _state.value.selectedIndicators.toMutableList()
        currentIndicators[index] = indicator
        _state.value = _state.value.copy(selectedIndicators = currentIndicators)
    }

    fun onSaveStrategy(name: String) {
        viewModelScope.launch {
            val newStrategy = Strategy(
                id = System.currentTimeMillis().toString(),
                name = name,
                coin = state.value.selectedCoin,
                timeframe = state.value.selectedTimeframe,
                indicators = state.value.selectedIndicators,
                isActive = false,
                createdAt = System.currentTimeMillis()
            )
            
            saveStrategyUseCase(
                strategy = newStrategy,
                takeProfitPercentage = state.value.takeProfitPercentage,
                stopLossPercentage = state.value.stopLossPercentage,
                tradeAmount = state.value.tradeAmount
            )

            _state.value = _state.value.copy(selectedIndicators = emptyList())
        }
    }

    fun onEditStrategy(strategy: Strategy) {
        currentStrategy = strategy
        _state.value = _state.value.copy(
            selectedCoin = strategy.coin,
            selectedTimeframe = strategy.timeframe,
            selectedIndicators = strategy.indicators,
            takeProfitPercentage = strategy.takeProfitPercentage,
            stopLossPercentage = strategy.stopLossPercentage,
            tradeAmount = strategy.tradeAmount
        )
    }

    fun onTakeProfitChange(value: Float?) {
        _state.value = _state.value.copy(takeProfitPercentage = value)
        currentStrategy?.let { strategy ->
            viewModelScope.launch {
                updateStrategySettingsUseCase(
                    id = strategy.id,
                    takeProfitPercentage = value,
                    stopLossPercentage = state.value.stopLossPercentage,
                    tradeAmount = state.value.tradeAmount
                )
            }
        }
    }

    fun onStopLossChange(value: Float?) {
        _state.value = _state.value.copy(stopLossPercentage = value)
        currentStrategy?.let { strategy ->
            viewModelScope.launch {
                updateStrategySettingsUseCase(
                    id = strategy.id,
                    takeProfitPercentage = state.value.takeProfitPercentage,
                    stopLossPercentage = value,
                    tradeAmount = state.value.tradeAmount
                )
            }
        }
    }

    fun onTradeAmountChange(value: Float?) {
        _state.value = _state.value.copy(tradeAmount = value)
        currentStrategy?.let { strategy ->
            viewModelScope.launch {
                updateStrategySettingsUseCase(
                    id = strategy.id,
                    takeProfitPercentage = state.value.takeProfitPercentage,
                    stopLossPercentage = state.value.stopLossPercentage,
                    tradeAmount = value
                )
            }
        }
    }

    data class StrategyState(
        val selectedCoin: String = "BTC",
        val selectedTimeframe: String = "1h",
        val currentPrice: Double = 0.0,
        val volume24h: Double = 0.0,
        val lastUpdateTime: Long = 0,
        val selectedIndicators: List<Indicator> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val takeProfitPercentage: Float? = null,
        val stopLossPercentage: Float? = null,
        val tradeAmount: Float? = null
    )

    companion object {
        val availableCoins = listOf("BTC", "ETH", "SOL", "AVAX", "FET", "RNDR")
        val availableTimeframes = listOf(
            Pair("1d", "1 Günlük"),
            Pair("4h", "4 Saatlik"),
            Pair("1h", "1 Saatlik"),
            Pair("15m", "15 Dakikalık"),
            Pair("5m", "5 Dakikalık")
        )
    }
} 