package com.example.cryptotradebot.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cryptotradebot.presentation.composable.CandlestickCard
import com.example.cryptotradebot.presentation.composable.CryptoBottomNavigation
import com.example.cryptotradebot.presentation.viewmodel.TradeViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeScreen(
    navController: NavController,
    viewModel: TradeViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    Scaffold(
        bottomBar = {
            CryptoBottomNavigation(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Üst Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.Default.ArrowBack, "Geri")
                }
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

            // Interval seçim dropdown'u
            ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = { },
            ) {
                TextField(
                    value = TradeViewModel.availableIntervals.find { it.first == state.selectedInterval }?.second ?: "",
                    onValueChange = { },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = false,
                    onDismissRequest = { }
                ) {
                    TradeViewModel.availableIntervals.forEach { interval ->
                        DropdownMenuItem(
                            text = { Text(interval.second) },
                            onClick = { viewModel.onIntervalSelect(interval.first) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Candlestick grafiği
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally)
                )
            } else {
                CandlestickCard(
                    candlesticks = state.candlesticks,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
} 