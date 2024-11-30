package com.example.cryptotradebot.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cryptotradebot.domain.model.Indicator
import com.example.cryptotradebot.domain.model.Strategy

@Entity(tableName = "strategies")
data class StrategyEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val coin: String,
    val timeframe: String,
    val indicators: List<Indicator>,
    val isActive: Boolean,
    val createdAt: Long,
    val takeProfitPercentage: Float?,
    val stopLossPercentage: Float?,
    val tradeAmount: Float?
)

// Extension functions for domain model conversion
fun StrategyEntity.toDomainModel() = Strategy(
    id = id,
    name = name,
    coin = coin,
    timeframe = timeframe,
    indicators = indicators,
    isActive = isActive,
    createdAt = createdAt,
    takeProfitPercentage = takeProfitPercentage,
    stopLossPercentage = stopLossPercentage,
    tradeAmount = tradeAmount
)

fun Strategy.toEntity(
    takeProfitPercentage: Float? = this.takeProfitPercentage,
    stopLossPercentage: Float? = this.stopLossPercentage,
    tradeAmount: Float? = this.tradeAmount
) = StrategyEntity(
    id = id,
    name = name,
    coin = coin,
    timeframe = timeframe,
    indicators = indicators,
    isActive = isActive,
    createdAt = createdAt,
    takeProfitPercentage = takeProfitPercentage,
    stopLossPercentage = stopLossPercentage,
    tradeAmount = tradeAmount
) 