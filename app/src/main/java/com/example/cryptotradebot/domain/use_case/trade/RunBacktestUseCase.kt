package com.example.cryptotradebot.domain.use_case.trade

import com.example.cryptotradebot.data.remote.dto.request.BacktestRequest
import com.example.cryptotradebot.data.remote.dto.response.BacktestResponse
import com.example.cryptotradebot.domain.repository.TradeRepository
import com.example.cryptotradebot.utils.Resource
import javax.inject.Inject

class RunBacktestUseCase @Inject constructor(
    private val repository: TradeRepository
) {
    suspend operator fun invoke(request: BacktestRequest): Resource<BacktestResponse> {
        return try {
            val result = repository.runBacktest(request)
            result.fold(
                onSuccess = { Resource.Success(it) },
                onFailure = { Resource.Error(it.message ?: "Beklenmeyen bir hata oluştu") }
            )
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Beklenmeyen bir hata oluştu")
        }
    }
} 