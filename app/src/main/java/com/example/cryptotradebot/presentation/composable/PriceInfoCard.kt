package com.example.cryptotradebot.presentation.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cryptotradebot.domain.model.Candlestick

@Composable
fun PriceInfoCard(
    candlestick: Candlestick,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            PriceRow("Açılış", candlestick.open)
            PriceRow("En Yüksek", candlestick.high)
            PriceRow("En Düşük", candlestick.low)
            PriceRow("Kapanış", candlestick.close)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            PriceRow("Hacim", candlestick.volume)
            PriceRow("İşlem Sayısı", candlestick.numberOfTrades.toString())
        }
    }
}

@Composable
private fun PriceRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
} 