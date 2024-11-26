package com.example.cryptotradebot.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cryptotradebot.presentation.viewmodel.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Üst Başlık ve Yenileme Butonu
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${state.selectedCoin}/USDT",
                style = MaterialTheme.typography.headlineMedium
            )
            IconButton(onClick = { viewModel.refreshData() }) {
                Icon(Icons.Default.Refresh, "Yenile")
            }
        }

        // Son Güncelleme Zamanı
        if (state.lastUpdateTime > 0) {
            Text(
                text = "Son Güncelleme: ${dateFormat.format(Date(state.lastUpdateTime))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Coin Seçimi
        Text(
            text = "Coin Seçimi",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            items(DashboardViewModel.availableCoins) { coin ->
                FilterChip(
                    selected = state.selectedCoin == coin,
                    onClick = { viewModel.onCoinSelect(coin) },
                    label = { Text(coin) }
                )
            }
        }

        // Zaman Aralığı Seçimi
        Text(
            text = "Zaman Aralığı",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            items(DashboardViewModel.availableIntervals) { interval ->
                FilterChip(
                    selected = state.selectedInterval == interval.first,
                    onClick = { viewModel.onIntervalSelect(interval.first) },
                    label = { Text(interval.second) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Fiyat Bilgileri
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        state.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        state.candlestick?.let { candlestick ->
            Card(
                modifier = Modifier.fillMaxWidth(),
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

        Spacer(modifier = Modifier.weight(1f))

        // Alt Navigasyon Butonları
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { navController.navigate("trade") },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Trade'e Git")
            }
            Button(
                onClick = { navController.navigate("backtest") },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Backtest'e Git")
            }
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