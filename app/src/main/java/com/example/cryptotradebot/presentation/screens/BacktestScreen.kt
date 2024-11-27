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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cryptotradebot.domain.model.Strategy
import com.example.cryptotradebot.presentation.composable.CryptoBottomNavigation
import com.example.cryptotradebot.presentation.navigation.Screen
import com.example.cryptotradebot.presentation.viewmodel.StrategyViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BacktestScreen(
    navController: NavController,
    viewModel: StrategyViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    Scaffold(
        bottomBar = {
            CryptoBottomNavigation(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Başlık
            Text(
                text = "Kayıtlı Stratejiler",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.savedStrategies) { strategy ->
                    StrategyCard(
                        strategy = strategy,
                        onEditClick = {
                            navController.currentBackStackEntry?.savedStateHandle?.set("selectedCoin", strategy.coin)
                            navController.currentBackStackEntry?.savedStateHandle?.set("selectedTimeframe", strategy.timeframe)
                            navController.navigate(Screen.Strategy.route)
                            viewModel.onEditStrategy(strategy)
                        },
                        onToggleClick = { viewModel.onToggleStrategy(strategy) },
                        onBacktestClick = { /* TODO: Backtest işlemi eklenecek */ }
                    )
                }
            }
        }
    }
}

@Composable
private fun StrategyCard(
    strategy: Strategy,
    onEditClick: () -> Unit,
    onToggleClick: () -> Unit,
    onBacktestClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
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
                    // Düzenle butonu
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, "Düzenle")
                    }
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
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    // Backtest butonu
                    IconButton(onClick = onBacktestClick) {
                        Icon(Icons.Default.DateRange, "Backtest")
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
                    onClick = { },
                    label = {
                        Text(if (strategy.isActive) "Aktif" else "Pasif")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (strategy.isActive) Icons.Default.CheckCircle else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (strategy.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
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