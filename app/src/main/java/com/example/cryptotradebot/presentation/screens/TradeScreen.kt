package com.example.cryptotradebot.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cryptotradebot.presentation.composable.*
import com.example.cryptotradebot.presentation.navigation.Screen
import com.example.cryptotradebot.presentation.viewmodel.TradeViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TradeScreen(
    navController: NavController,
    viewModel: TradeViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = {
            CryptoBottomNavigation(navController = navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.currentBackStackEntry?.savedStateHandle?.set("selectedCoin", state.selectedCoin)
                    navController.currentBackStackEntry?.savedStateHandle?.set("selectedTimeframe", state.selectedInterval)
                    navController.navigate(Screen.Strategy.route)
                }
            ) {
                Icon(Icons.Default.Build, "Strateji Oluştur")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            // Üst Bar
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
                items(TradeViewModel.availableCoins) { coin ->
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
                items(TradeViewModel.availableIntervals) { interval ->
                    FilterChip(
                        selected = state.selectedInterval == interval.first,
                        onClick = { viewModel.onIntervalSelect(interval.first) },
                        label = { Text(interval.second) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Loading ve Error durumları
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

            // Mum Grafiği
            if (state.candlesticks.isNotEmpty()) {
                CandlestickCard(candlesticks = state.candlesticks)
                
                Spacer(modifier = Modifier.height(16.dp))

                // Son Mum Detayları
                state.currentCandlestick?.let { candlestick ->
                    PriceInfoCard(candlestick = candlestick)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
} 