package com.example.cryptotradebot.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cryptotradebot.R
import com.example.cryptotradebot.presentation.composable.*
import com.example.cryptotradebot.presentation.navigation.Screen
import com.example.cryptotradebot.presentation.viewmodel.TradeViewModel

@Composable
fun TradeScreen(
    navController: NavController,
    viewModel: TradeViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
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
                Icon(Icons.Default.Build, stringResource(R.string.trade_create_strategy))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // Coin ve fiyat bilgileri
            CoinPriceHeader(
                coin = state.selectedCoin,
                timeframe = state.selectedInterval,
                price = state.currentCandlestick?.close?.toDouble() ?: 0.0,
                volume = state.currentCandlestick?.volume?.toDouble() ?: 0.0,
                lastUpdateTime = state.lastUpdateTime,
                availableCoins = TradeViewModel.availableCoins,
                availableTimeframes = TradeViewModel.availableIntervals(LocalContext.current),
                onCoinSelect = viewModel::onCoinSelect,
                onTimeframeSelect = viewModel::onIntervalSelect,
                modifier = Modifier.padding(16.dp)
            )

            // Loading ve Error durumları
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Mum Grafiği
            if (state.candlesticks.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
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
                            text = stringResource(R.string.trade_price_chart),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        CandlestickCard(candlesticks = state.candlesticks)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                // Son Mum Detayları
                state.currentCandlestick?.let { candlestick ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
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
                                text = stringResource(R.string.trade_price_details),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            PriceInfoCard(candlestick = candlestick)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
} 