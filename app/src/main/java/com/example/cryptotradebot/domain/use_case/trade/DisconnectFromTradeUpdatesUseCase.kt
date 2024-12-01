package com.example.cryptotradebot.domain.use_case.trade

import com.example.cryptotradebot.domain.repository.TradeRepository
import javax.inject.Inject

class DisconnectFromTradeUpdatesUseCase @Inject constructor(
    private val repository: TradeRepository
) {
    operator fun invoke() {
        repository.disconnectFromTradeUpdates()
    }
} 