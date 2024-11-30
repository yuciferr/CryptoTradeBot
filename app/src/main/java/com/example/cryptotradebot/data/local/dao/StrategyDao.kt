package com.example.cryptotradebot.data.local.dao

import androidx.room.*
import com.example.cryptotradebot.data.local.entity.StrategyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StrategyDao {
    @Query("SELECT * FROM strategies ORDER BY createdAt DESC")
    fun getAllStrategies(): Flow<List<StrategyEntity>>

    @Query("SELECT * FROM strategies WHERE id = :id")
    suspend fun getStrategyById(id: String): StrategyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStrategy(strategy: StrategyEntity)

    @Delete
    suspend fun deleteStrategy(strategy: StrategyEntity)

    @Query("UPDATE strategies SET isActive = :isActive WHERE id = :id")
    suspend fun updateStrategyActiveStatus(id: String, isActive: Boolean)

    @Query("UPDATE strategies SET takeProfitPercentage = :takeProfit, stopLossPercentage = :stopLoss, tradeAmount = :tradeAmount WHERE id = :id")
    suspend fun updateStrategyTradeSettings(id: String, takeProfit: Float?, stopLoss: Float?, tradeAmount: Float?)
} 