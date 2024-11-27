package com.example.cryptotradebot.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cryptotradebot.presentation.screens.AuthScreen
import com.example.cryptotradebot.presentation.screens.BacktestScreen
import com.example.cryptotradebot.presentation.screens.DashboardScreen
import com.example.cryptotradebot.presentation.screens.StrategyScreen
import com.example.cryptotradebot.presentation.screens.TradeScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Auth.route
    ) {
        composable(Screen.Auth.route) {
            AuthScreen(navController)
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController)
        }
        composable(Screen.Trade.route) {
            TradeScreen(navController)
        }
        composable(Screen.Strategy.route) {
            StrategyScreen(
                navController = navController,
                coin = navController.previousBackStackEntry?.savedStateHandle?.get<String>("selectedCoin") ?: "BTC",
                timeframe = navController.previousBackStackEntry?.savedStateHandle?.get<String>("selectedTimeframe") ?: "1h"
            )
        }
        composable(Screen.Backtest.route) {
            BacktestScreen(navController)
        }
    }
} 