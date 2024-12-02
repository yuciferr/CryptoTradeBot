package com.example.cryptotradebot.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BacktestResponse(
    val summary: BacktestSummary,
    val trades: List<BacktestTrade>
)

data class BacktestSummary(
    @SerializedName("initial_balance")
    val initialBalance: Double,
    @SerializedName("final_balance")
    val finalBalance: Double,
    @SerializedName("total_profit_loss")
    val totalProfitLoss: Double,
    @SerializedName("total_profit_loss_percentage")
    val totalProfitLossPercentage: Double,
    @SerializedName("total_trades")
    val totalTrades: Int,
    @SerializedName("winning_trades")
    val winningTrades: Int,
    @SerializedName("losing_trades")
    val losingTrades: Int,
    @SerializedName("win_rate")
    val winRate: Double,
    @SerializedName("max_drawdown")
    val maxDrawdown: Double,
    @SerializedName("sharpe_ratio")
    val sharpeRatio: Double,
    @SerializedName("risk_reward_ratio")
    val riskRewardRatio: Double,
    @SerializedName("average_profit_per_trade")
    val averageProfitPerTrade: Double
)

data class BacktestTrade(
    @SerializedName("entry_time")
    val entryTime: String,
    @SerializedName("exit_time")
    val exitTime: String,
    @SerializedName("entry_price")
    val entryPrice: Double,
    @SerializedName("exit_price")
    val exitPrice: Double,
    @SerializedName("profit_loss")
    val profitLoss: Double,
    @SerializedName("profit_percentage")
    val profitPercentage: Double,
    @SerializedName("exit_type")
    val exitType: String
) 