package com.example.cryptotradebot.presentation.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cryptotradebot.R
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
            PriceRow(stringResource(R.string.price_info_open), candlestick.open)
            PriceRow(stringResource(R.string.price_info_high), candlestick.high)
            PriceRow(stringResource(R.string.price_info_low), candlestick.low)
            PriceRow(stringResource(R.string.price_info_close), candlestick.close)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            PriceRow(stringResource(R.string.price_info_volume), candlestick.volume)
            PriceRow(stringResource(R.string.price_info_trades), candlestick.numberOfTrades.toString())
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