package com.example.cryptotradebot.domain.use_case.strategy

import com.example.cryptotradebot.domain.model.Strategy
import com.example.cryptotradebot.domain.repository.StrategyRepository
import javax.inject.Inject

class DeleteStrategyUseCase @Inject constructor(
    private val repository: StrategyRepository
) {
    suspend operator fun invoke(strategy: Strategy) {
        repository.deleteStrategy(strategy)
    }
} 