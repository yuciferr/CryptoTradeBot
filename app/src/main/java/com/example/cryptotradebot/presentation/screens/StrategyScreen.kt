package com.example.cryptotradebot.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cryptotradebot.presentation.composable.*
import com.example.cryptotradebot.presentation.viewmodel.StrategyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StrategyScreen(
    navController: NavController,
    coin: String,
    timeframe: String,
    viewModel: StrategyViewModel = hiltViewModel()
) {
    LaunchedEffect(coin, timeframe) {
        viewModel.onCoinSelect(coin)
        viewModel.onTimeframeSelect(timeframe)
    }

    val state = viewModel.state.value
    var showNewStrategyDialog by remember { mutableStateOf(false) }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Coin ve fiyat bilgileri
            CoinPriceHeader(
                coin = state.selectedCoin,
                timeframe = state.selectedTimeframe,
                price = state.currentPrice,
                volume = state.volume24h,
                lastUpdateTime = state.lastUpdateTime,
                availableCoins = StrategyViewModel.availableCoins,
                availableTimeframes = StrategyViewModel.availableTimeframes,
                onCoinSelect = viewModel::onCoinSelect,
                onTimeframeSelect = viewModel::onTimeframeSelect,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // İndikatör editörü
            IndicatorEditor(
                selectedIndicators = state.selectedIndicators,
                onAddIndicator = viewModel::onAddIndicator,
                onRemoveIndicator = viewModel::onRemoveIndicator,
                onUpdateIndicator = viewModel::onUpdateIndicator,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Strateji kaydetme butonu
            Button(
                onClick = { showNewStrategyDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Strateji Kaydet")
            }

            // Kayıtlı stratejiler
            SavedStrategiesList(
                strategies = state.savedStrategies,
                onEditStrategy = viewModel::onEditStrategy,
                onToggleStrategy = viewModel::onToggleStrategy,
                modifier = Modifier.weight(1f)
            )
        }

        // Yeni strateji isim girme dialog
        if (showNewStrategyDialog) {
            var strategyName by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showNewStrategyDialog = false },
                title = { Text("Strateji Adı") },
                text = {
                    TextField(
                        value = strategyName,
                        onValueChange = { strategyName = it },
                        label = { Text("Strateji adını girin") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (strategyName.isNotBlank()) {
                                viewModel.onSaveStrategy(strategyName)
                                showNewStrategyDialog = false
                            }
                        }
                    ) {
                        Text("Kaydet")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showNewStrategyDialog = false }) {
                        Text("İptal")
                    }
                }
            )
        }
    }
} 