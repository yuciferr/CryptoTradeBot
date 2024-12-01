package com.example.cryptotradebot.domain.use_case.trade

import com.example.cryptotradebot.domain.repository.TradeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTradeUpdatesUseCase @Inject constructor(
    private val repository: TradeRepository
) {
    operator fun invoke(): Flow<String> {
        return repository.getTradeUpdates()
    }
} 