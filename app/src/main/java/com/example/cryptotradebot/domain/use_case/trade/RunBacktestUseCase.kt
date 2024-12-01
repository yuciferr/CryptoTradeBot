package com.example.cryptotradebot.domain.use_case.trade

import com.example.cryptotradebot.data.remote.dto.BacktestRequest
import com.example.cryptotradebot.domain.repository.TradeRepository
import javax.inject.Inject

class RunBacktestUseCase @Inject constructor(
    private val repository: TradeRepository
) {
    suspend operator fun invoke(request: BacktestRequest): Result<Map<String, Any>> {
        return repository.runBacktest(request)
    }
} 