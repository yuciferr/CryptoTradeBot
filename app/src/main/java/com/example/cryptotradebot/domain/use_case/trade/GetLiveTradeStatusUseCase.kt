package com.example.cryptotradebot.domain.use_case.trade

import com.example.cryptotradebot.domain.repository.TradeRepository
import javax.inject.Inject

class GetLiveTradeStatusUseCase @Inject constructor(
    private val repository: TradeRepository
) {
    suspend operator fun invoke(): Result<Map<String, Any>> {
        return repository.getLiveTradeStatus()
    }
} 