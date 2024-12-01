package com.example.cryptotradebot.domain.use_case.trade

import com.example.cryptotradebot.domain.repository.TradeRepository
import javax.inject.Inject

class StartLiveTradeUseCase @Inject constructor(
    private val repository: TradeRepository
) {
    suspend operator fun invoke(request: Map<String, Any>): Result<Map<String, Any>> {
        return repository.startLiveTrade(request)
    }
} 