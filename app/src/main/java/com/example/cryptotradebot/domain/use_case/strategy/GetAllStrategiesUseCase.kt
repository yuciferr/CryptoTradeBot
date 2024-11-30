package com.example.cryptotradebot.domain.use_case.strategy

import com.example.cryptotradebot.domain.model.Strategy
import com.example.cryptotradebot.domain.repository.StrategyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllStrategiesUseCase @Inject constructor(
    private val repository: StrategyRepository
) {
    operator fun invoke(): Flow<List<Strategy>> {
        return repository.getAllStrategies()
    }
} 