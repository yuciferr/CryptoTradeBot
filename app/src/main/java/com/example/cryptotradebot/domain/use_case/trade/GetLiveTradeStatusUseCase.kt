package com.example.cryptotradebot.domain.use_case.trade

import SessionResponse
import com.example.cryptotradebot.domain.repository.TradeRepository
import com.example.cryptotradebot.utils.Resource
import javax.inject.Inject

class GetLiveTradeStatusUseCase @Inject constructor(
    private val repository: TradeRepository
) {
    suspend operator fun invoke(symbol: String? = null): Resource<List<SessionResponse>> {
        return try {
            val result = repository.getLiveTradeStatus(symbol)
            result.fold(
                onSuccess = { Resource.Success(it) },
                onFailure = { Resource.Error(it.message ?: "Beklenmeyen bir hata oluştu") }
            )
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Beklenmeyen bir hata oluştu")
        }
    }
} 