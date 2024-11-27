package com.example.cryptotradebot.domain.model

data class Indicator(
    val id: String,
    val name: String,
    val parameters: List<IndicatorParameter>,
    val triggerCondition: TriggerCondition
)

data class IndicatorParameter(
    val name: String,
    val value: Double,
    val minValue: Double,
    val maxValue: Double,
    val step: Double = 1.0
)

data class TriggerCondition(
    val type: TriggerType,
    val value: Double,
    val compareValue: Double? = null
)

enum class TriggerType {
    CROSSES_ABOVE,
    CROSSES_BELOW,
    GREATER_THAN,
    LESS_THAN,
    EQUALS,
    BETWEEN
}

// Mock indikatör listesi
object IndicatorList {
    val availableIndicators = listOf(
        Indicator(
            id = "rsi",
            name = "RSI",
            parameters = listOf(
                IndicatorParameter("Period", 14.0, 2.0, 50.0, 1.0),
                IndicatorParameter("Overbought", 70.0, 50.0, 100.0, 1.0),
                IndicatorParameter("Oversold", 30.0, 0.0, 50.0, 1.0)
            ),
            triggerCondition = TriggerCondition(TriggerType.CROSSES_BELOW, 30.0)
        ),
        Indicator(
            id = "macd",
            name = "MACD",
            parameters = listOf(
                IndicatorParameter("Fast Period", 12.0, 2.0, 50.0, 1.0),
                IndicatorParameter("Slow Period", 26.0, 2.0, 100.0, 1.0),
                IndicatorParameter("Signal Period", 9.0, 2.0, 50.0, 1.0)
            ),
            triggerCondition = TriggerCondition(TriggerType.CROSSES_ABOVE, 0.0)
        ),
        Indicator(
            id = "ema",
            name = "EMA",
            parameters = listOf(
                IndicatorParameter("Period", 20.0, 2.0, 200.0, 1.0)
            ),
            triggerCondition = TriggerCondition(TriggerType.CROSSES_ABOVE, 0.0)
        ),
        Indicator(
            id = "bb",
            name = "Bollinger Bands",
            parameters = listOf(
                IndicatorParameter("Period", 20.0, 2.0, 100.0, 1.0),
                IndicatorParameter("Standard Deviation", 2.0, 1.0, 5.0, 0.1)
            ),
            triggerCondition = TriggerCondition(TriggerType.CROSSES_BELOW, 0.0)
        )
    )
} 