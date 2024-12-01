package com.example.cryptotradebot.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cryptotradebot.domain.model.Strategy
import com.example.cryptotradebot.presentation.composable.CryptoBottomNavigation
import com.example.cryptotradebot.presentation.navigation.Screen
import com.example.cryptotradebot.presentation.viewmodel.BacktestViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BacktestScreen(
    navController: NavController,
    viewModel: BacktestViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val gson = remember { Gson() }

    Scaffold(
        bottomBar = {
            CryptoBottomNavigation(navController = navController)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "Kayıtlı Stratejiler",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            items(state.strategies) { strategy ->
                BacktestStrategyCard(
                    strategy = strategy,
                    onEditClick = {
                        with(navController.currentBackStackEntry?.savedStateHandle) {
                            this?.set("strategyId", strategy.id)
                            this?.set("strategyName", strategy.name)
                            this?.set("strategyCoin", strategy.coin)
                            this?.set("strategyTimeframe", strategy.timeframe)
                            this?.set("strategyTakeProfit", strategy.takeProfitPercentage)
                            this?.set("strategyStopLoss", strategy.stopLossPercentage)
                            this?.set("strategyTradeAmount", strategy.tradeAmount)
                            this?.set("strategyIndicators", gson.toJson(strategy.indicators))
                        }
                        navController.navigate(Screen.Strategy.route)
                    },
                    onToggleClick = { viewModel.onToggleStrategy(strategy) },
                    onLogClick = { 
                        with(navController.currentBackStackEntry?.savedStateHandle) {
                            this?.set("selectedStrategyId", strategy.id)
                            this?.set("showBacktestOnly", true)
                            this?.set("strategyCoin", strategy.coin)
                            this?.set("strategyTimeframe", strategy.timeframe)
                            this?.set("strategyName", strategy.name)
                        }
                        navController.navigate("log_screen/${URLEncoder.encode(strategy.name, "UTF-8")}")
                    },
                    onDeleteClick = { viewModel.onDeleteStrategy(strategy) },
                    navController = navController
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BacktestStrategyCard(
    strategy: Strategy,
    onEditClick: () -> Unit,
    onToggleClick: () -> Unit,
    onLogClick: () -> Unit,
    onDeleteClick: () -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Stratejiyi Sil") },
            text = { Text("${strategy.name} stratejisini silmek istediğinizden emin misiniz?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClick()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Sil", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onLogClick,
        colors = CardDefaults.cardColors(
            containerColor = if (strategy.isActive) 
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Üst kısım - Strateji adı ve durum
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strategy.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Başlat/Durdur butonu
                    IconButton(onClick = onToggleClick) {
                        if (strategy.isActive) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Durdur",
                                tint = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "Başlat",
                                tint = Color(0xFF4CAF50)
                            )
                        }
                    }
                    // edit butonu
                    IconButton(onClick = onEditClick) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Düzenle",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    // silme butonu
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Sil",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Coin ve timeframe
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${strategy.coin}/USDT",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = strategy.timeframe,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Trade Settings
            if (strategy.takeProfitPercentage != null || strategy.stopLossPercentage != null || strategy.tradeAmount != null) {
                Text(
                    text = "Trade Ayarları:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
                
                strategy.takeProfitPercentage?.let {
                    Text(
                        text = "• Take Profit: %${String.format("%.2f", it)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF4CAF50)
                    )
                }
                
                strategy.stopLossPercentage?.let {
                    Text(
                        text = "• Stop Loss: %${String.format("%.2f", it)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                
                strategy.tradeAmount?.let {
                    Text(
                        text = "• İşlem Miktarı: ${String.format("%.2f", it)} USDT",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // İndikatör listesi
            Text(
                text = "İndikatörler:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
            strategy.indicators.forEach { indicator ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "• ${indicator.name}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = formatTriggerCondition(indicator.triggerCondition),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Durum ve oluşturulma zamanı
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = onToggleClick,
                    label = {
                        Text(if (strategy.isActive) "Aktif" else "Pasif")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (strategy.isActive) Icons.Default.CheckCircle else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (strategy.isActive) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        leadingIconContentColor = if (strategy.isActive) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
                        labelColor = if (strategy.isActive) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                    )
                )
                Text(
                    text = dateFormat.format(Date(strategy.createdAt)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

private fun formatTriggerCondition(condition: com.example.cryptotradebot.domain.model.TriggerCondition): String {
    return when (condition.type) {
        com.example.cryptotradebot.domain.model.TriggerType.CROSSES_ABOVE -> "↗ ${condition.value}"
        com.example.cryptotradebot.domain.model.TriggerType.CROSSES_BELOW -> "↘ ${condition.value}"
        com.example.cryptotradebot.domain.model.TriggerType.GREATER_THAN -> "> ${condition.value}"
        com.example.cryptotradebot.domain.model.TriggerType.LESS_THAN -> "< ${condition.value}"
        com.example.cryptotradebot.domain.model.TriggerType.EQUALS -> "= ${condition.value}"
        com.example.cryptotradebot.domain.model.TriggerType.BETWEEN -> "${condition.value} - ${condition.compareValue}"
    }
} 