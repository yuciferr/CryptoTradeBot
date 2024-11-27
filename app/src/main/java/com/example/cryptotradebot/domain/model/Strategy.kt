package com.example.cryptotradebot.domain.model

data class Strategy(
    val id: String,
    val name: String,
    val coin: String,
    val timeframe: String,
    val indicators: List<Indicator>,
    val isActive: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

// Mock strateji listesi
object StrategyList {
    val mockStrategies = listOf(
        Strategy(
            id = "1",
            name = "RSI Bounce Strategy",
            coin = "BTC",
            timeframe = "1h",
            indicators = listOf(
                IndicatorList.availableIndicators.first { it.id == "rsi" }.copy(
                    triggerCondition = TriggerCondition(TriggerType.CROSSES_BELOW, 30.0)
                )
            ),
            isActive = true
        ),
        Strategy(
            id = "2",
            name = "MACD Cross with EMA",
            coin = "ETH",
            timeframe = "4h",
            indicators = listOf(
                IndicatorList.availableIndicators.first { it.id == "macd" }.copy(
                    triggerCondition = TriggerCondition(TriggerType.CROSSES_ABOVE, 0.0)
                ),
                IndicatorList.availableIndicators.first { it.id == "ema" }.copy(
                    triggerCondition = TriggerCondition(TriggerType.GREATER_THAN, 0.0)
                )
            ),
            isActive = false
        ),
        Strategy(
            id = "3",
            name = "Bollinger Squeeze",
            coin = "SOL",
            timeframe = "15m",
            indicators = listOf(
                IndicatorList.availableIndicators.first { it.id == "bb" }.copy(
                    triggerCondition = TriggerCondition(TriggerType.BETWEEN, 0.0, 1.0)
                )
            ),
            isActive = true
        )
    )
} 