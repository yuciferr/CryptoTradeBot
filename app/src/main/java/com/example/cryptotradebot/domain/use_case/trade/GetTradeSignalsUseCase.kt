package com.example.cryptotradebot.domain.use_case.trade

import WebSocketSignal
import com.example.cryptotradebot.domain.repository.TradeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTradeSignalsUseCase @Inject constructor(
    private val repository: TradeRepository
) {
    operator fun invoke(): Flow<WebSocketSignal> {
        return repository.getTradeSignals()
    }
} 