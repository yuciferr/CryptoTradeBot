package com.example.cryptotradebot.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptotradebot.domain.model.Strategy
import com.example.cryptotradebot.domain.use_case.strategy.GetAllStrategiesUseCase
import com.example.cryptotradebot.domain.use_case.strategy.ToggleStrategyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BacktestViewModel @Inject constructor(
    private val getAllStrategiesUseCase: GetAllStrategiesUseCase,
    private val toggleStrategyUseCase: ToggleStrategyUseCase
) : ViewModel() {

    private val _state = mutableStateOf(BacktestState())
    val state: State<BacktestState> = _state

    init {
        getStrategies()
    }

    private fun getStrategies() {
        getAllStrategiesUseCase()
            .onEach { strategies ->
                _state.value = _state.value.copy(
                    strategies = strategies,
                    isLoading = false
                )
            }
            .launchIn(viewModelScope)
    }

    fun onToggleStrategy(strategy: Strategy) {
        viewModelScope.launch {
            toggleStrategyUseCase(strategy.id, !strategy.isActive)
        }
    }

    data class BacktestState(
        val strategies: List<Strategy> = emptyList(),
        val isLoading: Boolean = true,
        val error: String? = null
    )
} 