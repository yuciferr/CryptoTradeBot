package com.example.cryptotradebot.presentation.composable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.cryptotradebot.presentation.navigation.Screen

@Composable
fun CryptoBottomNavigation(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route

        // Dashboard
        NavigationBarItem(
            selected = currentRoute == Screen.Dashboard.route,
            onClick = {
                if (currentRoute != Screen.Dashboard.route) {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            },
            icon = {
                Icon(
                    imageVector = if (currentRoute == Screen.Dashboard.route) {
                        Icons.Filled.Home
                    } else {
                        Icons.Outlined.Home
                    },
                    contentDescription = "Dashboard"
                )
            },
            label = { Text("Dashboard") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        )

        // Trade
        NavigationBarItem(
            selected = currentRoute == Screen.Strategy.route,
            onClick = {
                if (currentRoute != Screen.Strategy.route) {
                    navController.navigate(Screen.Strategy.route) {
                        popUpTo(Screen.Dashboard.route)
                    }
                }
            },
            icon = {
                Icon(
                    imageVector = if (currentRoute == Screen.Strategy.route) {
                        Icons.Filled.Build
                    } else {
                        Icons.Outlined.Build
                    },
                    contentDescription = "Trade"
                )
            },
            label = { Text("Trade") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        )

        // Backtest
        NavigationBarItem(
            selected = currentRoute == Screen.Backtest.route,
            onClick = {
                if (currentRoute != Screen.Backtest.route) {
                    navController.navigate(Screen.Backtest.route) {
                        popUpTo(Screen.Dashboard.route)
                    }
                }
            },
            icon = {
                Icon(
                    imageVector = if (currentRoute == Screen.Backtest.route) {
                        Icons.Filled.DateRange
                    } else {
                        Icons.Outlined.DateRange
                    },
                    contentDescription = "Backtest"
                )
            },
            label = { Text("Backtest") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        )
    }
} 