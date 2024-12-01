package com.example.cryptotradebot.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cryptotradebot.R
import com.example.cryptotradebot.presentation.composable.*

@Composable
fun DashboardScreen(
    navController: NavController
) {
    // Mock Data
    val mockWalletData = object {
        val totalBalance = 10000.0
        val dailyProfitPercent = 2.5
        val dailyProfitAmount = 250.0
    }

    val mockAssets = listOf(
        AssetInfo("BTC", 0.15, 4500.0, 30000.0),
        AssetInfo("ETH", 2.5, 3000.0, 1200.0),
        AssetInfo("SOL", 50.0, 1000.0, 20.0),
        AssetInfo("AVAX", 40.0, 800.0, 20.0),
        AssetInfo("FET", 1000.0, 450.0, 0.45),
        AssetInfo("RNDR", 200.0, 250.0, 1.25)
    )

    val mockBots = listOf(
        ActiveBotInfo(
            "BTC",
            29500.0,
            150.0,
            System.currentTimeMillis() - 86400000,
            0.05,
            1475.0,
            "EMA Cross"
        ),
        ActiveBotInfo(
            "ETH",
            1150.0,
            -50.0,
            System.currentTimeMillis() - 43200000,
            1.0,
            1150.0,
            "RSI Bounce"
        ),
        ActiveBotInfo(
            "SOL",
            19.5,
            25.0,
            System.currentTimeMillis() - 21600000,
            25.0,
            487.5,
            "MACD Divergence"
        )
    )

    Scaffold(
        bottomBar = {
            CryptoBottomNavigation(navController = navController)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cüzdan Özeti
            item {
                Spacer(modifier = Modifier.height(16.dp))
                WalletSummaryCard(
                    totalBalance = mockWalletData.totalBalance,
                    dailyProfitPercent = mockWalletData.dailyProfitPercent,
                    dailyProfitAmount = mockWalletData.dailyProfitAmount
                )
            }

            // Varlık Listesi Başlığı
            item {
                Text(
                    text = stringResource(R.string.dashboard_assets_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Varlık Listesi
            items(mockAssets) { asset ->
                AssetListItem(asset = asset)
            }

            // Aktif Botlar Başlığı
            item {
                Text(
                    text = stringResource(R.string.dashboard_active_bots_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Aktif Botlar
            items(mockBots) { bot ->
                ActiveBotCard(bot = bot)
            }

            // En altta boşluk
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
} 