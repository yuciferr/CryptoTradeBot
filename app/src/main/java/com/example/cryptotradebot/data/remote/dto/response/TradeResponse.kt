data class TradeResponse(
    val message: String,
    val session_id: Int,
    val status: TradeStatus
)

data class TradeStatus(
    val is_running: Boolean,
    val balance: Double,
    val profit_loss: Double,
    val profit_loss_percentage: Double,
    val total_trades: Int,
    val winning_trades: Int,
    val losing_trades: Int,
    val current_position: String?,
    val last_price: Double,
    val last_update: String
) 