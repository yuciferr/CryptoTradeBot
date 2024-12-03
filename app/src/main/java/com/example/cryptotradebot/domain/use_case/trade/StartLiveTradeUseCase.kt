package com.example.cryptotradebot.domain.use_case.trade

import TradeRequest
import TradeResponse
import com.example.cryptotradebot.domain.repository.TradeRepository
import com.example.cryptotradebot.utils.Resource
import javax.inject.Inject

class StartLiveTradeUseCase @Inject constructor(
    private val repository: TradeRepository
) {
    suspend operator fun invoke(request: TradeRequest): Resource<TradeResponse> {
        return try {
            val result = repository.startLiveTrade(request)
            result.fold(
                onSuccess = { Resource.Success(it) },
                onFailure = { Resource.Error(it.message ?: "Beklenmeyen bir hata oluştu") }
            )
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Beklenmeyen bir hata oluştu")
        }
    }
} 