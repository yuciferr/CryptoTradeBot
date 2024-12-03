data class SessionResponse(
    val session: Session,
    val signals: Signals
)

data class Session(
    val id: Int,
    val symbol: String,
    val initial_balance: Double,
    val current_balance: Double,
    val total_trades: Int,
    val winning_trades: Int,
    val losing_trades: Int,
    val win_rate: Double,
    val started_at: String,
    val last_update: String
)

data class Signals(
    val buy: List<Signal>,
    val sell: List<Signal>
)

data class Signal(
    val timestamp: String,
    val price: Double
) 