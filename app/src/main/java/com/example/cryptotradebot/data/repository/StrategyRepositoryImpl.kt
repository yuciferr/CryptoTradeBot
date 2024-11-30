package com.example.cryptotradebot.data.repository

import com.example.cryptotradebot.data.local.dao.StrategyDao
import com.example.cryptotradebot.data.local.entity.toDomainModel
import com.example.cryptotradebot.data.local.entity.toEntity
import com.example.cryptotradebot.domain.model.Strategy
import com.example.cryptotradebot.domain.repository.StrategyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StrategyRepositoryImpl @Inject constructor(
    private val dao: StrategyDao
) : StrategyRepository {

    override fun getAllStrategies(): Flow<List<Strategy>> {
        return dao.getAllStrategies().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getStrategyById(id: String): Strategy? {
        return dao.getStrategyById(id)?.toDomainModel()
    }

    override suspend fun insertStrategy(
        strategy: Strategy,
        takeProfitPercentage: Float?,
        stopLossPercentage: Float?,
        tradeAmount: Float?
    ) {
        dao.insertStrategy(
            strategy.toEntity(
                takeProfitPercentage = takeProfitPercentage,
                stopLossPercentage = stopLossPercentage,
                tradeAmount = tradeAmount
            )
        )
    }

    override suspend fun deleteStrategy(strategy: Strategy) {
        dao.deleteStrategy(strategy.toEntity())
    }

    override suspend fun updateStrategyActiveStatus(id: String, isActive: Boolean) {
        dao.updateStrategyActiveStatus(id, isActive)
    }

    override suspend fun updateStrategyTradeSettings(
        id: String,
        takeProfitPercentage: Float?,
        stopLossPercentage: Float?,
        tradeAmount: Float?
    ) {
        dao.updateStrategyTradeSettings(
            id = id,
            takeProfit = takeProfitPercentage,
            stopLoss = stopLossPercentage,
            tradeAmount = tradeAmount
        )
    }
} 