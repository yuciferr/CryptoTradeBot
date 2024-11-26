package com.example.cryptotradebot.presentation.navigation

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Dashboard : Screen("dashboard")
    object Trade : Screen("trade")
    object Backtest : Screen("backtest")

    companion object {
        fun getStartDestination() = Auth.route
    }
} 