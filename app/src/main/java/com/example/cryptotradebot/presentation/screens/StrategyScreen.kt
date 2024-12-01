package com.example.cryptotradebot.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cryptotradebot.domain.model.Indicator
import com.example.cryptotradebot.domain.model.Strategy
import com.example.cryptotradebot.presentation.composable.CoinPriceHeader
import com.example.cryptotradebot.presentation.composable.IndicatorEditor
import com.example.cryptotradebot.presentation.navigation.Screen
import com.example.cryptotradebot.presentation.viewmodel.StrategyViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StrategyScreen(
    navController: NavController,
    coin: String,
    timeframe: String,
    viewModel: StrategyViewModel = hiltViewModel()
) {
    val savedStateHandle = navController.previousBackStackEntry?.savedStateHandle
    val strategyId = savedStateHandle?.get<String>("strategyId")
    val gson = remember { Gson() }
    
    LaunchedEffect(strategyId) {
        if (strategyId != null) {
            // Düzenleme modu
            val indicatorsJson = savedStateHandle.get<String>("strategyIndicators")
            val indicators = if (!indicatorsJson.isNullOrEmpty()) {
                val type = object : TypeToken<List<Indicator>>() {}.type
                gson.fromJson<List<Indicator>>(indicatorsJson, type)
            } else {
                emptyList()
            }
            
            viewModel.initEditMode(
                id = strategyId,
                name = savedStateHandle.get<String>("strategyName") ?: "",
                coin = savedStateHandle.get<String>("strategyCoin") ?: "",
                timeframe = savedStateHandle.get<String>("strategyTimeframe") ?: "",
                takeProfitPercentage = savedStateHandle.get<Float>("strategyTakeProfit"),
                stopLossPercentage = savedStateHandle.get<Float>("strategyStopLoss"),
                tradeAmount = savedStateHandle.get<Float>("strategyTradeAmount"),
                indicators = indicators
            )
        } else {
            // Yeni strateji modu
            viewModel.onCoinSelect(coin)
            viewModel.onTimeframeSelect(timeframe)
        }
    }

    val state = viewModel.state.value
    var showSaveDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showSaveDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.CheckCircle, "Strateji Kaydet")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                // Geri butonu ve başlık
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, "Geri")
                    }
                    Text(
                        text = "Strateji Oluştur",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
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
            }

            item {
                // Trading Ayarları Bölümü
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Trading Ayarları",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        // Take Profit Ayarı
                        OutlinedTextField(
                            value = state.takeProfitPercentage?.toString() ?: "",
                            onValueChange = { viewModel.onTakeProfitChange(it.toFloatOrNull()) },
                            label = { Text("Take Profit (%)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            singleLine = true
                        )
                        
                        // Stop Loss Ayarı
                        OutlinedTextField(
                            value = state.stopLossPercentage?.toString() ?: "",
                            onValueChange = { viewModel.onStopLossChange(it.toFloatOrNull()) },
                            label = { Text("Stop Loss (%)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            singleLine = true
                        )
                        
                        // İşlem Miktarı
                        OutlinedTextField(
                            value = state.tradeAmount?.toString() ?: "",
                            onValueChange = { viewModel.onTradeAmountChange(it.toFloatOrNull()) },
                            label = { Text("İşlem Miktarı (USDT)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            singleLine = true
                        )
                    }
                }
            }

            item {
                // İndikatör editörü
                IndicatorEditor(
                    selectedIndicators = state.selectedIndicators,
                    onAddIndicator = viewModel::onAddIndicator,
                    onRemoveIndicator = viewModel::onRemoveIndicator,
                    onUpdateIndicator = viewModel::onUpdateIndicator,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Alt kısımda boşluk bırak
            item {
                Spacer(modifier = Modifier.height(80.dp)) // FAB için alan
            }
        }

        // Kaydetme dialogu
        if (showSaveDialog) {
            var strategyName by remember { mutableStateOf(state.strategyName) }
            AlertDialog(
                onDismissRequest = { showSaveDialog = false },
                containerColor = MaterialTheme.colorScheme.surface,
                title = { 
                    Text(
                        if (strategyId != null) "Stratejiyi Düzenle" else "Yeni Strateji",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                text = {
                    Column {
                        Text(
                            if (strategyId != null) "Strateji ismini düzenleyin" else "Stratejiniz için bir isim belirleyin",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        OutlinedTextField(
                            value = strategyName,
                            onValueChange = { strategyName = it },
                            label = { Text("Strateji Adı") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (strategyId != null) {
                                viewModel.onUpdateStrategy(strategyName)
                            } else {
                                viewModel.onSaveStrategy(strategyName)
                            }
                            showSaveDialog = false
                            navController.navigate(Screen.Backtest.route) {
                                popUpTo(Screen.Backtest.route) { inclusive = true }
                            }
                        },
                        enabled = strategyName.isNotBlank()
                    ) {
                        Text(if (strategyId != null) "Güncelle" else "Kaydet")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSaveDialog = false }) {
                        Text("İptal")
                    }
                }
            )
        }
    }
} 