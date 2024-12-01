package com.example.cryptotradebot.data.remote.dto

data class BacktestRequest(
    val symbol: String,
    val timeframe: String,
    val initialBalance: Double = 10000.0,
    val indicatorSettings: IndicatorSettings,
    val riskManagement: RiskManagement? = null
)

data class IndicatorSettings(
    val rsi: RSISettings? = null,
    val macd: MACDSettings? = null,
    val bollinger: BollingerSettings? = null,
    val sma: SMASettings? = null,
    val ema: EMASettings? = null
)

data class RSISettings(
    val period: Int = 14,
    val overbought: Int = 70,
    val oversold: Int = 30
)

data class MACDSettings(
    val fast: Int = 12,
    val slow: Int = 26,
    val signal: Int = 9
)

data class BollingerSettings(
    val period: Int = 20,
    val std: Int = 2,
    val windowDev: Int = 2,
    val movingAvg: String = "sma"
)

data class SMASettings(
    val periods: List<Int> = listOf(20)
)

data class EMASettings(
    val periods: List<Int> = listOf(20),
    val smoothing: Int = 2
)

data class RiskManagement(
    val stopLoss: Double = 1.5,
    val takeProfit: Double = 2.0
) 