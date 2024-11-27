package com.example.cryptotradebot.presentation.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cryptotradebot.domain.model.Strategy
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SavedStrategiesList(
    strategies: List<Strategy>,
    onEditStrategy: (Strategy) -> Unit,
    onToggleStrategy: (Strategy) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Kayıtlı Stratejiler",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(strategies) { strategy ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Üst kısım - Strateji adı ve durum
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strategy.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(onClick = { onEditStrategy(strategy) }) {
                                Icon(Icons.Default.Edit, "Düzenle")
                            }
                            IconButton(onClick = { onToggleStrategy(strategy) }) {
                                if (strategy.isActive) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Durdur",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.PlayArrow,
                                        contentDescription = "Başlat",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    // Coin ve timeframe
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${strategy.coin}/USDT",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = strategy.timeframe,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // İndikatör listesi
                    Text(
                        text = "İndikatörler:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                    strategy.indicators.forEach { indicator ->
                        Text(
                            text = "• ${indicator.name}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Oluşturulma zamanı
                    Text(
                        text = "Oluşturulma: ${dateFormat.format(Date(strategy.createdAt))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
} 