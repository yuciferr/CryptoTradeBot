package com.example.cryptotradebot.domain.model

object IndicatorList {
    val availableIndicators = listOf(
        // Trend İndikatörleri
        Indicator(
            name = "SMA",
            category = "Trend",
            parameters = listOf(
                Parameter(
                    name = "Period",
                    value = 14.0,
                    minValue = 2.0,
                    maxValue = 200.0,
                    description = "Time period for calculation"
                )
            ),
            triggerCondition = TriggerCondition(TriggerType.CROSSES_ABOVE, 0.0)
        ),
        Indicator(
            name = "EMA",
            category = "Trend",
            parameters = listOf(
                Parameter(
                    name = "Period",
                    value = 20.0,
                    minValue = 2.0,
                    maxValue = 200.0,
                    description = "Time period for calculation"
                )
            ),
            triggerCondition = TriggerCondition(TriggerType.CROSSES_ABOVE, 0.0)
        ),
        Indicator(
            name = "ADX",
            category = "Trend",
            parameters = listOf(
                Parameter(
                    name = "Period",
                    value = 14.0,
                    minValue = 2.0,
                    maxValue = 50.0,
                    description = "Time period for calculation"
                )
            ),
            triggerCondition = TriggerCondition(TriggerType.GREATER_THAN, 25.0)
        ),
        Indicator(
            name = "SuperTrend",
            category = "Trend",
            parameters = listOf(
                Parameter(
                    name = "Period",
                    value = 10.0,
                    minValue = 2.0,
                    maxValue = 50.0,
                    description = "Time period for calculation"
                ),
                Parameter(
                    name = "Multiplier",
                    value = 3.0,
                    minValue = 1.0,
                    maxValue = 10.0,
                    step = 0.1,
                    description = "ATR multiplier"
                )
            ),
            triggerCondition = TriggerCondition(TriggerType.CROSSES_ABOVE, 0.0)
        ),

        // Momentum İndikatörleri
        Indicator(
            name = "RSI",
            category = "Momentum",
            parameters = listOf(
                Parameter(
                    name = "Period",
                    value = 14.0,
                    minValue = 2.0,
                    maxValue = 50.0,
                    description = "Time period for calculation"
                )
            ),
            triggerCondition = TriggerCondition(TriggerType.CROSSES_BELOW, 30.0)
        ),
        Indicator(
            name = "MACD",
            category = "Momentum",
            parameters = listOf(
                Parameter(
                    name = "Fast Period",
                    value = 12.0,
                    minValue = 2.0,
                    maxValue = 50.0,
                    description = "Fast EMA period"
                ),
                Parameter(
                    name = "Slow Period",
                    value = 26.0,
                    minValue = 2.0,
                    maxValue = 100.0,
                    description = "Slow EMA period"
                ),
                Parameter(
                    name = "Signal Period",
                    value = 9.0,
                    minValue = 2.0,
                    maxValue = 50.0,
                    description = "Signal line period"
                )
            ),
            triggerCondition = TriggerCondition(TriggerType.CROSSES_ABOVE, 0.0)
        ),
        Indicator(
            name = "CCI",
            category = "Momentum",
            parameters = listOf(
                Parameter(
                    name = "Period",
                    value = 20.0,
                    minValue = 2.0,
                    maxValue = 50.0,
                    description = "Time period for calculation"
                )
            ),
            triggerCondition = TriggerCondition(TriggerType.CROSSES_BELOW, -100.0)
        ),
        Indicator(
            name = "Stochastic",
            category = "Momentum",
            parameters = listOf(
                Parameter(
                    name = "Fast K Period",
                    value = 14.0,
                    minValue = 2.0,
                    maxValue = 50.0,
                    description = "Fast %K period"
                ),
                Parameter(
                    name = "Slow K Period",
                    value = 3.0,
                    minValue = 1.0,
                    maxValue = 10.0,
                    description = "Slow %K period"
                ),
                Parameter(
                    name = "Slow D Period",
                    value = 3.0,
                    minValue = 1.0,
                    maxValue = 10.0,
                    description = "Slow %D period"
                ),
                Parameter(
                    name = "MA Type",
                    value = 0.0,
                    minValue = 0.0,
                    maxValue = 3.0,
                    step = 1.0,
                    description = "Moving average type (0:SMA, 1:EMA, 2:WMA, 3:DEMA)"
                )
            ),
            triggerCondition = TriggerCondition(TriggerType.CROSSES_ABOVE, 20.0)
        ),

        // Volatilite İndikatörleri
        Indicator(
            name = "Bollinger Bands",
            category = "Volatility",
            parameters = listOf(
                Parameter(
                    name = "Period",
                    value = 20.0,
                    minValue = 2.0,
                    maxValue = 100.0,
                    description = "Time period for calculation"
                ),
                Parameter(
                    name = "Upper Deviation",
                    value = 2.0,
                    minValue = 1.0,
                    maxValue = 5.0,
                    step = 0.1,
                    description = "Upper band standard deviation"
                ),
                Parameter(
                    name = "Lower Deviation",
                    value = 2.0,
                    minValue = 1.0,
                    maxValue = 5.0,
                    step = 0.1,
                    description = "Lower band standard deviation"
                )
            ),
            triggerCondition = TriggerCondition(TriggerType.CROSSES_BELOW, 0.0)
        ),
        Indicator(
            name = "ATR",
            category = "Volatility",
            parameters = listOf(
                Parameter(
                    name = "Period",
                    value = 14.0,
                    minValue = 2.0,
                    maxValue = 50.0,
                    description = "Time period for calculation"
                )
            ),
            triggerCondition = TriggerCondition(TriggerType.GREATER_THAN, 0.0)
        ),

        // Hacim İndikatörü
        Indicator(
            name = "OBV",
            category = "Volume",
            parameters = listOf(),  // OBV'nin parametresi yok
            triggerCondition = TriggerCondition(TriggerType.GREATER_THAN, 0.0)
        )
    )
} 