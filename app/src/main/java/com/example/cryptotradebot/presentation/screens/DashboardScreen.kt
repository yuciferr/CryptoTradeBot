package com.example.cryptotradebot.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cryptotradebot.presentation.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "BTC/USDT Son Veriler",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        if (state.isLoading) {
            CircularProgressIndicator()
        }
        
        state.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        
        state.candlestick?.let { candlestick ->
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Açılış: ${candlestick.open}")
                Text("En Yüksek: ${candlestick.high}")
                Text("En Düşük: ${candlestick.low}")
                Text("Kapanış: ${candlestick.close}")
                Text("Hacim: ${candlestick.volume}")
                Text("İşlem Sayısı: ${candlestick.numberOfTrades}")
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = { navController.navigate("trade") }) {
                Text(text = "Trade'e Git")
            }
            Button(onClick = { navController.navigate("backtest") }) {
                Text(text = "Backtest'e Git")
            }
        }
    }
} 