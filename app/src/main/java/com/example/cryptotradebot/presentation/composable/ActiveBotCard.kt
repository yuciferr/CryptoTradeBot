package com.example.cryptotradebot.presentation.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cryptotradebot.R
import com.example.cryptotradebot.presentation.theme.GainColor
import com.example.cryptotradebot.presentation.theme.LossColor
import java.text.SimpleDateFormat
import java.util.*

data class ActiveBotInfo(
    val coinName: String,
    val entryPrice: Double,
    val currentProfit: Double,
    val openTime: Long,
    val positionSizeCoin: Double,
    val positionSizeUsdt: Double,
    val strategyName: String
)

@Composable
fun ActiveBotCard(
    bot: ActiveBotInfo,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Üst kısım - Coin adı ve strateji
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = bot.coinName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = bot.strategyName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Giriş fiyatı ve kar/zarar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.active_bot_entry_price),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = stringResource(R.string.active_bot_price_format, bot.entryPrice),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                    Text(
                        text = stringResource(R.string.active_bot_profit_loss),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    val profitColor = if (bot.currentProfit >= 0) GainColor else LossColor
                    val profitPrefix = if (bot.currentProfit >= 0) "+" else ""
                    Text(
                        text = stringResource(R.string.active_bot_profit_format, profitPrefix, bot.currentProfit),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = profitColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Pozisyon boyutu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.active_bot_position_size),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = stringResource(R.string.active_bot_position_size_coin_format, bot.positionSizeCoin, bot.coinName),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = stringResource(R.string.active_bot_position_size_usdt_format, bot.positionSizeUsdt),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                // Açılış zamanı
                Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                    Text(
                        text = stringResource(R.string.active_bot_open_time),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = dateFormat.format(Date(bot.openTime)),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
} 