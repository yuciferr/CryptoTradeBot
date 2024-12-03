import com.example.cryptotradebot.data.remote.dto.request.IndicatorSettings
import com.example.cryptotradebot.data.remote.dto.request.RiskManagement

data class TradeRequest(
    val symbol: String,
    val initial_balance: Double,
    val indicator_settings: IndicatorSettings,
    val risk_management: RiskManagement
)
