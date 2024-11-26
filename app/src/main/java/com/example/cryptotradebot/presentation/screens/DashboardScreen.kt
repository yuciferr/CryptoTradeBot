package com.example.cryptotradebot.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun DashboardScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "CryptoTradeBot",
            style = MaterialTheme.typography.headlineLarge
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { navController.navigate("trade") },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(text = "Trade'e Git")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { navController.navigate("backtest") },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(text = "Backtest'e Git")
        }
    }
} 