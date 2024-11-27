package com.example.cryptotradebot.presentation.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cryptotradebot.presentation.theme.GainColor
import com.example.cryptotradebot.presentation.theme.LossColor

@Composable
fun WalletSummaryCard(
    totalBalance: Double,
    dailyProfitPercent: Double,
    dailyProfitAmount: Double,
    modifier: Modifier = Modifier
) {
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
            Text(
                text = "Toplam Varlık",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Text(
                text = "$ ${String.format("%.2f", totalBalance)} USDT",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "24s Değişim:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                val profitColor = if (dailyProfitPercent >= 0) GainColor else LossColor
                val profitPrefix = if (dailyProfitPercent >= 0) "+" else ""
                
                Text(
                    text = "$profitPrefix${String.format("%.2f", dailyProfitPercent)}%",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = profitColor
                )
                
                Text(
                    text = "$profitPrefix$ ${String.format("%.2f", dailyProfitAmount)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = profitColor
                )
            }
        }
    }
} 