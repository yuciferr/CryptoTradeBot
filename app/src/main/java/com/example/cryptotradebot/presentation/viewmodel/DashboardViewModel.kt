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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getCandlesticksUseCase: GetCandlesticksUseCase
) : ViewModel() {

    private val _state = mutableStateOf(DashboardState())
    val state: State<DashboardState> = _state

    private var fetchJob: Job? = null

    init {
        startFetchingData()
    }

    private fun startFetchingData() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            while (true) {
                getCryptoData()
                delay(5000) // Her 5 saniyede bir güncelle
            }
        }
    }

    private fun getCryptoData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            when (val result = getCandlesticksUseCase(
                symbol = "BTCUSDT",
                interval = "1m",
                limit = 1
            )) {
                is Resource.Success -> {
                    result.data?.let { candlesticks ->
                        if (candlesticks.isNotEmpty()) {
                            _state.value = _state.value.copy(
                                candlestick = candlesticks.first(),
                                isLoading = false,
                                error = null
                            )
                        }
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

    override fun onCleared() {
        super.onCleared()
        fetchJob?.cancel()
    }

    data class DashboardState(
        val candlestick: Candlestick? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )
} 