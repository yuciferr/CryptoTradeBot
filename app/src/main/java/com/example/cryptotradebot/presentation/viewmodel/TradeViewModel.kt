package com.example.cryptotradebot.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptotradebot.domain.model.Candlestick
import com.example.cryptotradebot.domain.use_case.GetCandlesticksUseCase
import com.example.cryptotradebot.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TradeViewModel @Inject constructor(
    private val getCandlesticksUseCase: GetCandlesticksUseCase
) : ViewModel() {

    private val _state = mutableStateOf(TradeState())
    val state: State<TradeState> = _state

    private var fetchJob: Job? = null

    init {
        getCryptoData()
    }

    fun onCoinSelect(coin: String) {
        _state.value = _state.value.copy(selectedCoin = coin)
        getCryptoData()
    }

    fun onIntervalSelect(interval: String) {
        _state.value = _state.value.copy(selectedInterval = interval)
        getCryptoData()
    }

    fun refreshData() {
        getCryptoData()
    }

    private fun getCryptoData() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            when (val result = getCandlesticksUseCase(
                symbol = "${_state.value.selectedCoin}USDT",
                interval = _state.value.selectedInterval,
                limit = 100
            )) {
                is Resource.Success -> {
                    result.data?.let { candlesticks ->
                        _state.value = _state.value.copy(
                            candlesticks = candlesticks,
                            currentCandlestick = candlesticks.lastOrNull(),
                            lastUpdateTime = System.currentTimeMillis(),
                            isLoading = false,
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
    }

    data class TradeState(
        val candlesticks: List<Candlestick> = emptyList(),
        val currentCandlestick: Candlestick? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val selectedCoin: String = "BTC",
        val selectedInterval: String = "1h",
        val lastUpdateTime: Long = 0
    )

    companion object {
        val availableCoins = listOf("BTC", "ETH", "FET", "AVAX", "SOL", "RNDR")
        val availableIntervals = listOf(
            Pair("1d", "1 Günlük"),
            Pair("4h", "4 Saatlik"),
            Pair("1h", "1 Saatlik"),
            Pair("15m", "15 Dakikalık"),
            Pair("5m", "5 Dakikalık")
        )
    }
} 