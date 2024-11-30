package com.example.cryptotradebot.domain.use_case.strategy

import com.example.cryptotradebot.domain.repository.StrategyRepository
import javax.inject.Inject

class ToggleStrategyUseCase @Inject constructor(
    private val repository: StrategyRepository
) {
    suspend operator fun invoke(id: String, isActive: Boolean) {
        repository.updateStrategyActiveStatus(id, isActive)
    }
} 