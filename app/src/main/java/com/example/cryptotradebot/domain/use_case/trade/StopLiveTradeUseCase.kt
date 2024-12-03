package com.example.cryptotradebot.domain.use_case.trade

import CloseSessionResponse
import com.example.cryptotradebot.domain.repository.TradeRepository
import com.example.cryptotradebot.utils.Resource
import javax.inject.Inject

class StopLiveTradeUseCase @Inject constructor(
    private val repository: TradeRepository
) {
    suspend operator fun invoke(symbol: String? = null): Resource<CloseSessionResponse> {
        return try {
            val result = repository.stopLiveTrade(symbol)
            result.fold(
                onSuccess = { Resource.Success(it) },
                onFailure = { Resource.Error(it.message ?: "Beklenmeyen bir hata oluştu") }
            )
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Beklenmeyen bir hata oluştu")
        }
    }
} 