package com.example.cryptotradebot.presentation.composable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.cryptotradebot.R
import com.example.cryptotradebot.presentation.navigation.Screen

@Composable
fun CryptoBottomNavigation(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = stringResource(R.string.nav_dashboard_desc)) },
            label = { Text(stringResource(R.string.nav_dashboard)) },
            selected = currentRoute == Screen.Dashboard.route,
            onClick = {
                if (currentRoute != Screen.Dashboard.route) {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Build, contentDescription = stringResource(R.string.nav_trade_desc)) },
            label = { Text(stringResource(R.string.nav_trade)) },
            selected = currentRoute == Screen.Trade.route || currentRoute == Screen.Strategy.route,
            onClick = {
                if (currentRoute != Screen.Trade.route) {
                    navController.navigate(Screen.Trade.route) {
                        popUpTo(Screen.Trade.route) { inclusive = true }
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.DateRange, contentDescription = stringResource(R.string.nav_backtest_desc)) },
            label = { Text(stringResource(R.string.nav_backtest)) },
            selected = currentRoute == Screen.Backtest.route,
            onClick = {
                if (currentRoute != Screen.Backtest.route) {
                    navController.navigate(Screen.Backtest.route) {
                        popUpTo(Screen.Backtest.route) { inclusive = true }
                    }
                }
            }
        )
    }
} 