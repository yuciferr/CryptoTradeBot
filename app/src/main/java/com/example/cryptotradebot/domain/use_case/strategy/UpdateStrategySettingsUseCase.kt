package com.example.cryptotradebot.domain.use_case.strategy

import com.example.cryptotradebot.domain.repository.StrategyRepository
import javax.inject.Inject

class UpdateStrategySettingsUseCase @Inject constructor(
    private val repository: StrategyRepository
) {
    suspend operator fun invoke(
        id: String,
        takeProfitPercentage: Float?,
        stopLossPercentage: Float?,
        tradeAmount: Float?
    ) {
        repository.updateStrategyTradeSettings(
            id = id,
            takeProfitPercentage = takeProfitPercentage,
            stopLossPercentage = stopLossPercentage,
            tradeAmount = tradeAmount
        )
    }
} 