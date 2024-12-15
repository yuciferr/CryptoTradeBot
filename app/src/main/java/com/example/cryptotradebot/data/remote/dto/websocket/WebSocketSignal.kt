data class WebSocketSignal(
    val type: String,
    val message: Message
)

data class Message(
    val type: String,
    val signal: TradeSignal
)

data class TradeSignal(
    val symbol: String,
    val signal_type: String,
    val timestamp: String,
    val price: Double,
    val profit_loss: Double? = null,
    val profit_percentage: Double? = null
) 