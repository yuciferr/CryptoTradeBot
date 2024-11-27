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
    viewModel: StrategyViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    var showNewStrategyDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            CryptoBottomNavigation(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Coin ve fiyat bilgileri
            CoinPriceHeader(
                coin = state.selectedCoin,
                price = state.currentPrice,
                volume = state.volume24h,
                lastUpdateTime = state.lastUpdateTime,
                availableCoins = StrategyViewModel.availableCoins,
                onCoinSelect = viewModel::onCoinSelect,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Timeframe seçici
            ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                TextField(
                    value = StrategyViewModel.availableTimeframes.find { it.first == state.selectedTimeframe }?.second ?: "",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = false,
                    onDismissRequest = {}
                ) {
                    StrategyViewModel.availableTimeframes.forEach { (value, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = { viewModel.onTimeframeSelect(value) }
                        )
                    }
                }
            }

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