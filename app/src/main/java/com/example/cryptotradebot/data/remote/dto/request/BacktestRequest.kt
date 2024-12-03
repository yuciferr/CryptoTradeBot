package com.example.cryptotradebot.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class BacktestRequest(
    val symbol: String,
    val timeframe: String,
    @SerializedName("initial_balance")
    val initialBalance: Double = 10000.0,
    @SerializedName("indicator_settings")
    val indicatorSettings: IndicatorSettings,
    @SerializedName("risk_management")
    val riskManagement: RiskManagement? = null
)

data class IndicatorSettings(
    val rsi: RSISettings? = null,
    val macd: MACDSettings? = null,
    val bollinger: BollingerSettings? = null,
    val sma: SMASettings? = null,
    val ema: EMASettings? = null,
    val cci: CCISettings? = null,
    val adx: ADXSettings? = null,
    val supertrend: SupertrendSettings? = null
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
    @SerializedName("window_dev")
    val windowDev: Int = 2,
    @SerializedName("moving_avg")
    val movingAvg: String = "sma"
)

data class SMASettings(
    val periods: List<Int> = listOf(20)
)

data class EMASettings(
    val periods: List<Int> = listOf(20),
    val smoothing: Int = 2
)

data class CCISettings(
    val period: Int = 20,
    val oversold: Int = -100,
    val overbought: Int = 100
)

data class ADXSettings(
    val period: Int = 14,
    val threshold: Int = 25
)

data class SupertrendSettings(
    val period: Int = 10,
    val multiplier: Int = 3
)

data class RiskManagement(
    @SerializedName("stop_loss")
    val stopLoss: Double = 1.5,
    @SerializedName("take_profit")
    val takeProfit: Double = 2.0
) 