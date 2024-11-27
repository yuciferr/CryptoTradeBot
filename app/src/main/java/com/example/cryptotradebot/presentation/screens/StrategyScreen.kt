package com.example.cryptotradebot.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Strateji Oluştur") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Geri")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showNewStrategyDialog = true }
            ) {
                Icon(Icons.Default.CheckCircle, "Strateji Kaydet")
            }
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

            // İndikatör başlığı
            Text(
                text = "İndikatörler ve Tetikleyiciler",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // İndikatör editörü
            IndicatorEditor(
                selectedIndicators = state.selectedIndicators,
                onAddIndicator = viewModel::onAddIndicator,
                onRemoveIndicator = viewModel::onRemoveIndicator,
                onUpdateIndicator = viewModel::onUpdateIndicator,
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
                    OutlinedTextField(
                        value = strategyName,
                        onValueChange = { strategyName = it },
                        label = { Text("Strateji adını girin") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (strategyName.isNotBlank()) {
                                viewModel.onSaveStrategy(strategyName)
                                showNewStrategyDialog = false
                                navController.navigateUp()
                            }
                        },
                        enabled = strategyName.isNotBlank()
                    ) {
                        Text("Kaydet")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showNewStrategyDialog = false }) {
                        Text("İptal")
                    }
                }
            )
        }
    }
} 