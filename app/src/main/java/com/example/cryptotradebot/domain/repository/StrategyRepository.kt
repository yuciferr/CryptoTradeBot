package com.example.cryptotradebot.domain.repository

import com.example.cryptotradebot.domain.model.Strategy
import kotlinx.coroutines.flow.Flow

interface StrategyRepository {
    fun getAllStrategies(): Flow<List<Strategy>>
    
    suspend fun getStrategyById(id: String): Strategy?
    
    suspend fun insertStrategy(
        strategy: Strategy,
        takeProfitPercentage: Float?,
        stopLossPercentage: Float?,
        tradeAmount: Float?
    )
    
    suspend fun deleteStrategy(strategy: Strategy)
    
    suspend fun updateStrategyActiveStatus(id: String, isActive: Boolean)
    
    suspend fun updateStrategyTradeSettings(
        id: String,
        takeProfitPercentage: Float?,
        stopLossPercentage: Float?,
        tradeAmount: Float?
    )
} 