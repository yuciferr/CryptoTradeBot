package com.example.cryptotradebot.presentation.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cryptotradebot.domain.model.Candlestick

@Composable
fun CandlestickCard(
    candlesticks: List<Candlestick>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            CandlestickChart(
                candlesticks = candlesticks,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            )
        }
    }
} 