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
import com.example.cryptotradebot.domain.model.Candlestick
import com.example.cryptotradebot.domain.model.TradeLog
import com.example.cryptotradebot.domain.model.TradeType
import com.example.cryptotradebot.presentation.composable.CryptoBottomNavigation
import com.example.cryptotradebot.presentation.composable.CandlestickChart
import com.example.cryptotradebot.presentation.viewmodel.LogViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen(
    navController: NavController,
    viewModel: LogViewModel = hiltViewModel(),
    strategyTitle: String
) {
    val state = viewModel.state.value
    val candlesticks = viewModel.candlesticks.collectAsState().value
    val selectedCoin = viewModel.selectedCoin.collectAsState().value
    val selectedInterval = viewModel.selectedInterval.collectAsState().value
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Live", "Backtest")
    
    // Strateji bilgilerini al
    LaunchedEffect(key1 = Unit) {
        navController.currentBackStackEntry?.savedStateHandle?.let { handle ->
            handle.get<String>("strategyCoin")?.let { coin ->
                viewModel.onCoinSelect(coin)
            }
            handle.get<String>("strategyTimeframe")?.let { timeframe ->
                viewModel.onIntervalSelect(timeframe)
            }
            handle.get<Boolean>("showBacktestOnly")?.let { showBacktest ->
                if (showBacktest) {
                    selectedTabIndex = 1
                }
            }
        }
    }

    // Her sekme için çalışma durumu
    var isLiveRunning by remember { mutableStateOf(false) }
    var isBacktestRunning by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strategyTitle) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            // Ana içerik - LazyColumn
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Grafik
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(450.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "$selectedCoin/USDT - $selectedInterval",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                            )
                            if (state.isLoading) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            } else if (state.error != null) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = state.error,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            } else {
                                CandlestickChart(
                                    candlesticks = candlesticks,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                )
                            }
                        }
                    }
                }

                // 2. Başlat/Durdur Butonu
                item {
                    Button(
                        onClick = {
                            if (selectedTabIndex == 0) {
                                isLiveRunning = !isLiveRunning
                            } else {
                                isBacktestRunning = !isBacktestRunning
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if ((selectedTabIndex == 0 && isLiveRunning) || 
                                (selectedTabIndex == 1 && isBacktestRunning))
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            if ((selectedTabIndex == 0 && isLiveRunning) || 
                                (selectedTabIndex == 1 && isBacktestRunning))
                                Icons.Default.Clear
                            else
                                Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if ((selectedTabIndex == 0 && isLiveRunning) || 
                                (selectedTabIndex == 1 && isBacktestRunning))
                                "Durdur"
                            else
                                "Başlat"
                        )
                    }
                }

                // 3. Strateji Özeti Kartı
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Toplam İşlem",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "24",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Ortalama Kâr",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "+2.45%",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color(0xFF4CAF50)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Başarılı İşlem",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "18",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color(0xFF4CAF50)
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Başarı Oranı",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "75.0%",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color(0xFF4CAF50)
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. İşlem Logları
                val filteredLogs = mockTradeLogs.filter { log ->
                    when (selectedTabIndex) {
                        0 -> !log.isBacktest // Live işlemler
                        1 -> log.isBacktest  // Backtest işlemler
                        else -> true
                    }
                }
                
                items(filteredLogs) { log ->
                    TradeLogCard(log = log, dateFormat = dateFormat)
                }
            }
        }
    }
}

// Mock veriler
private val mockTradeLogs = listOf(
    TradeLog(
        id = "1",
        strategyId = "rsi_bb_btc_1h",
        strategyName = "RSI + BB",
        coin = "BTC",
        type = TradeType.BUY,
        price = 42150.0,
        amount = 0.1,
        total = 4215.0,
        timestamp = System.currentTimeMillis(),
        profit = null,
        isBacktest = false
    ),
    TradeLog(
        id = "2",
        strategyId = "rsi_bb_btc_1h",
        strategyName = "RSI + BB",
        coin = "BTC",
        type = TradeType.SELL,
        price = 42950.0,
        amount = 0.1,
        total = 4295.0,
        timestamp = System.currentTimeMillis() - 3600000,
        profit = 1.89,
        isBacktest = false
    ),
    // Daha fazla mock veri eklenebilir...
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TradeLogCard(
    log: TradeLog,
    dateFormat: SimpleDateFormat,
    modifier: Modifier = Modifier
) {
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
            // Üst kısım - Coin ve işlem tipi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${log.coin}/USDT",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    AssistChip(
                        onClick = { },
                        label = { Text(log.strategyName) },
                        colors = AssistChipDefaults.assistChipColors(
                            labelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                AssistChip(
                    onClick = { },
                    label = { Text(if (log.type == TradeType.BUY) "ALIŞ" else "SATIŞ") },
                    leadingIcon = {
                        Icon(
                            if (log.type == TradeType.BUY) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = if (log.type == TradeType.BUY) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        labelColor = if (log.type == TradeType.BUY) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Fiyat ve miktar bilgileri
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Fiyat",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = String.format("%.2f USDT", log.price),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Miktar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = String.format("%.4f ${log.coin}", log.amount),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Toplam ve kâr/zarar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Toplam",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = String.format("%.2f USDT", log.total),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                log.profit?.let { profit ->
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                text = String.format("%+.2f%%", profit),
                                color = if (profit >= 0) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                            )
                        }
                    )
                }
            }

            // Zaman
            Text(
                text = dateFormat.format(Date(log.timestamp)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}