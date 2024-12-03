package com.example.cryptotradebot.presentation.screens

import WebSocketSignal
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cryptotradebot.R
import com.example.cryptotradebot.domain.model.TradeLog
import com.example.cryptotradebot.domain.model.TradeType
import com.example.cryptotradebot.presentation.composable.CandlestickChart
import com.example.cryptotradebot.presentation.viewmodel.LogUiState
import com.example.cryptotradebot.presentation.viewmodel.LogViewModel
import java.text.SimpleDateFormat
import java.util.Locale

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
    val strategy = viewModel.strategy.collectAsState().value
    val isBacktestRunning = viewModel.isBacktestRunning.collectAsState().value
    val backtestResults = viewModel.backtestResults.collectAsState().value
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()) }
    var selectedTabIndex by remember { mutableStateOf(1) }
    val tabs = listOf(stringResource(R.string.log_live), stringResource(R.string.log_backtest))
    var isLiveRunning by remember { mutableStateOf(false) }
    
    // Strateji verilerini al ve ViewModel'e aktar
    LaunchedEffect(Unit) {
        navController.previousBackStackEntry?.savedStateHandle?.let { handle ->
            handle.get<String>("strategyJson")?.let { json ->
                android.util.Log.d("yuci", "LogScreen - Received StrategyJson: $json")
                viewModel.updateStrategy(json)
            }
            handle.get<String>("strategyCoin")?.let { coin ->
                android.util.Log.d("yuci", "LogScreen - Received Coin: $coin")
                viewModel.onCoinSelect(coin)
            }
            handle.get<String>("strategyTimeframe")?.let { timeframe ->
                android.util.Log.d("yuci", "LogScreen - Received Timeframe: $timeframe")
                viewModel.onIntervalSelect(timeframe)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strategyTitle) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, stringResource(R.string.log_back))
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

            // Ana içerik
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Grafik
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
                                text = stringResource(R.string.log_coin_timeframe_format, selectedCoin, selectedInterval),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                            )
                            when (val uiState = viewModel.uiState.collectAsState().value) {
                                is LogUiState.Loading -> {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                                is LogUiState.Error -> {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = uiState.message,
                                            color = MaterialTheme.colorScheme.error,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(bottom = 16.dp)
                                        )
                                        Button(
                                            onClick = { viewModel.retryLastRequest() },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary
                                            )
                                        ) {
                                            Text(stringResource(R.string.log_retry))
                                        }
                                    }
                                }
                                is LogUiState.Success -> {
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
                }

                // Live/Backtest Başlat/Durdur Butonları
                item {
                    if (selectedTabIndex == 0) {
                        // Live Trading Button
                        Button(
                            onClick = { isLiveRunning = !isLiveRunning },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isLiveRunning)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                if (isLiveRunning) Icons.Default.Clear else Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (isLiveRunning)
                                    stringResource(R.string.log_stop)
                                else
                                    stringResource(R.string.log_start)
                            )
                        }
                    } else {
                        // Backtest Button
                        Button(
                            onClick = {
                                if (isBacktestRunning) {
                                    viewModel.stopBacktest()
                                } else {
                                    viewModel.startBacktest()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isBacktestRunning)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isBacktestRunning) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onError,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Icon(
                                if (isBacktestRunning) Icons.Default.Clear else Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (isBacktestRunning)
                                    stringResource(R.string.log_stop)
                                else
                                    stringResource(R.string.log_start)
                            )
                        }
                    }
                }

                // Backtest Sonuçları (sadece backtest sekmesinde ve sonuçlar varsa göster)
                if (selectedTabIndex == 1 && backtestResults.isNotEmpty()) {
                    item {
                        BacktestResultsCard(
                            backtestResults = backtestResults,
                            dateFormat = dateFormat
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BacktestResultsCard(
    backtestResults: List<TradeLog>,
    dateFormat: SimpleDateFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.log_backtest_results),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // İstatistikler
            val totalTrades = backtestResults.size
            val successfulTrades = backtestResults.count { it.profit != null && it.profit > 0 }
            val totalProfit = backtestResults.mapNotNull { it.profit }.sum()
            val winRate = if (totalTrades > 0) (successfulTrades.toFloat() / totalTrades) * 100 else 0f

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatisticItem(
                    title = stringResource(R.string.log_total_trades),
                    value = totalTrades.toString()
                )
                StatisticItem(
                    title = stringResource(R.string.log_successful_trades),
                    value = successfulTrades.toString(),
                    valueColor = Color(0xFF4CAF50)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatisticItem(
                    title = stringResource(R.string.log_total_profit),
                    value = stringResource(R.string.log_profit_format, totalProfit),
                    valueColor = if (totalProfit >= 0) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                )
                StatisticItem(
                    title = stringResource(R.string.log_win_rate),
                    value = stringResource(R.string.log_percentage_format, winRate),
                    valueColor = Color(0xFF4CAF50)
                )
            }

            // İşlem Listesi
            Text(
                text = stringResource(R.string.log_trade_history),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            backtestResults.forEach { trade ->
                TradeLogItem(trade = trade, dateFormat = dateFormat)
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

@Composable
private fun StatisticItem(
    title: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = valueColor
        )
    }
}

@Composable
private fun TradeLogItem(
    trade: TradeLog,
    dateFormat: SimpleDateFormat
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = dateFormat.format(trade.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = stringResource(
                    if (trade.type == TradeType.BUY) R.string.log_buy_format else R.string.log_sell_format,
                    trade.amount,
                    trade.coin
                ),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = stringResource(R.string.log_price_format, trade.price),
                style = MaterialTheme.typography.bodyMedium
            )
            trade.profit?.let { profit ->
                Text(
                    text = stringResource(R.string.log_profit_format, profit),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (profit >= 0) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun LiveTradingSection(
    viewModel: LogViewModel,
    liveTradeState: LogViewModel.LiveTradeState,
    tradeSignals: List<WebSocketSignal>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Live Trading Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.log_live_trading_status),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                when (liveTradeState) {
                    is LogViewModel.LiveTradeState.Idle -> {
                        Text(stringResource(R.string.log_live_trading_idle))
                    }
                    is LogViewModel.LiveTradeState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                    is LogViewModel.LiveTradeState.Running -> {
                        val status = liveTradeState.tradeResponse.status
                        Column {
                            LiveTradeStatusItem(
                                title = stringResource(R.string.log_balance),
                                value = stringResource(R.string.log_price_format, status.balance)
                            )
                            LiveTradeStatusItem(
                                title = stringResource(R.string.log_profit_loss),
                                value = stringResource(R.string.log_profit_format, status.profit_loss),
                                valueColor = if (status.profit_loss >= 0) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                            )
                            LiveTradeStatusItem(
                                title = stringResource(R.string.log_total_trades),
                                value = status.total_trades.toString()
                            )
                            LiveTradeStatusItem(
                                title = stringResource(R.string.log_win_rate),
                                value = stringResource(R.string.log_percentage_format, 
                                    if (status.total_trades > 0) 
                                        (status.winning_trades.toFloat() / status.total_trades) * 100 
                                    else 0f
                                )
                            )
                            status.current_position?.let { position ->
                                LiveTradeStatusItem(
                                    title = stringResource(R.string.log_current_position),
                                    value = position
                                )
                            }
                        }
                    }
                    is LogViewModel.LiveTradeState.Error -> {
                        Text(
                            text = liveTradeState.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Trade Signals Card
        if (tradeSignals.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.log_trade_signals),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    LazyColumn {
                        items(tradeSignals) { signal ->
                            TradeSignalItem(signal = signal)
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LiveTradeStatusItem(
    title: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor
        )
    }
}

@Composable
private fun TradeSignalItem(signal: WebSocketSignal) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = signal.signal.timestamp,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = signal.signal.signal_type,
                style = MaterialTheme.typography.bodyMedium,
                color = when (signal.signal.signal_type.lowercase()) {
                    "buy" -> Color(0xFF4CAF50)
                    "sell" -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
        }
        Text(
            text = stringResource(R.string.log_price_format, signal.signal.price),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}