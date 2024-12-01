package com.example.cryptotradebot.presentation.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptotradebot.R
import com.example.cryptotradebot.domain.model.Candlestick
import com.example.cryptotradebot.domain.use_case.GetCandlesticksUseCase
import com.example.cryptotradebot.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TradeViewModel @Inject constructor(
    private val getCandlesticksUseCase: GetCandlesticksUseCase,
    @ApplicationContext private val context: Context
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
        val availableCoins = listOf("BTC", "ETH", "FET", "AVAX", "SOL", "RENDER")
        fun availableIntervals(context: Context) = listOf(
            Pair("1d", context.getString(R.string.interval_1d)),
            Pair("4h", context.getString(R.string.interval_4h)),
            Pair("1h", context.getString(R.string.interval_1h)),
            Pair("15m", context.getString(R.string.interval_15m)),
            Pair("5m", context.getString(R.string.interval_5m)),
            Pair("1m", context.getString(R.string.interval_1m))
        )
    }
} 