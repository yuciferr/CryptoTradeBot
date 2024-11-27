package com.example.cryptotradebot.presentation.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class AssetInfo(
    val coinName: String,
    val amount: Double,
    val valueInUsdt: Double,
    val pricePerCoin: Double
)

@Composable
fun AssetListItem(
    asset: AssetInfo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sol taraf - Coin adı ve miktarı
            Column {
                Text(
                    text = asset.coinName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = String.format("%.8f", asset.amount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            // Orta - Coin fiyatı
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$ ${String.format("%.2f", asset.pricePerCoin)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Sağ taraf - USDT değeri
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$ ${String.format("%.2f", asset.valueInUsdt)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
} 