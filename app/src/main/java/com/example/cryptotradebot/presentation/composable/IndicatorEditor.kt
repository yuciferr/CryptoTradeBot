package com.example.cryptotradebot.presentation.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cryptotradebot.domain.model.Indicator
import com.example.cryptotradebot.domain.model.IndicatorList
import com.example.cryptotradebot.domain.model.TriggerCondition
import com.example.cryptotradebot.domain.model.TriggerType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndicatorEditor(
    selectedIndicators: List<Indicator>,
    onAddIndicator: (Indicator) -> Unit,
    onRemoveIndicator: (Indicator) -> Unit,
    onUpdateIndicator: (Int, Indicator) -> Unit,
    modifier: Modifier = Modifier
) {
    var showIndicatorSelector by remember { mutableStateOf(false) }
    var editingIndicator by remember { mutableStateOf<Pair<Int, Indicator>?>(null) }

    Column(modifier = modifier) {
        // Başlık ve Ekle butonu
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "İndikatörler",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { showIndicatorSelector = true }) {
                Icon(Icons.Default.Add, "İndikatör Ekle")
            }
        }

        // Seçili indikatörler listesi
        selectedIndicators.forEachIndexed { index, indicator ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = indicator.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = indicator.category,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Row {
                            IconButton(onClick = { editingIndicator = index to indicator }) {
                                Icon(Icons.Default.Edit, "Düzenle")
                            }
                            IconButton(onClick = { onRemoveIndicator(indicator) }) {
                                Icon(Icons.Default.Delete, "Sil")
                            }
                        }
                    }

                    // Parametre değerleri
                    indicator.parameters.forEach { param ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = param.name,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = param.value.toString(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Tetikleme koşulu
                    Text(
                        text = "Signal: ${formatTriggerCondition(indicator.triggerCondition)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }

    // İndikatör seçim bottom sheet
    if (showIndicatorSelector) {
        ModalBottomSheet(onDismissRequest = { showIndicatorSelector = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Select Indicator",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Kategorilere göre gruplandırılmış indikatörler
                listOf("Trend", "Momentum", "Volume", "Volatility").forEach { category ->
                    Text(
                        text = category,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    IndicatorList.availableIndicators
                        .filter { it.category == category }
                        .forEach { indicator ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onAddIndicator(indicator)
                                        showIndicatorSelector = false
                                    },
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Text(
                                    text = indicator.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // İndikatör düzenleme bottom sheet
    editingIndicator?.let { (index, indicator) ->
        IndicatorParameterEditor(
            indicator = indicator,
            onDismiss = { editingIndicator = null },
            onSave = { updatedIndicator ->
                onUpdateIndicator(index, updatedIndicator)
                editingIndicator = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IndicatorParameterEditor(
    indicator: Indicator,
    onDismiss: () -> Unit,
    onSave: (Indicator) -> Unit
) {
    var parameters by remember { mutableStateOf(indicator.parameters) }
    var triggerCondition by remember { mutableStateOf(indicator.triggerCondition) }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "${indicator.name} Parametreleri",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Parametre sliderları
            parameters.forEachIndexed { index, param ->
                Text(
                    text = param.name,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                param.description?.let { desc ->
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Slider(
                    value = param.value.toFloat(),
                    onValueChange = { newValue ->
                        // Yeni değeri yuvarla
                        val roundedValue = if (param.step < 1) {
                            (newValue * 10).toInt() / 10.0
                        } else {
                            newValue.toInt().toDouble()
                        }
                        parameters = parameters.toMutableList().apply {
                            this[index] = param.copy(value = roundedValue)
                        }
                    },
                    valueRange = param.minValue.toFloat()..param.maxValue.toFloat(),
                    steps = ((param.maxValue - param.minValue) / param.step).toInt() - 1
                )
                Text(
                    text = if (param.step < 1) {
                        String.format("%.1f", param.value)
                    } else {
                        String.format("%.0f", param.value)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            // Sinyal değerleri düzenleme bölümü (RSI, CCI gibi indikatörler için)
            if (indicator.hasEditableSignalValues) {
                Text(
                    text = "Sinyal Değerleri",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                when (indicator.name) {
                    "RSI" -> {
                        // RSI için özel sinyal değerleri
                        var overbought by remember { mutableStateOf(triggerCondition.compareValue ?: 70.0) }
                        var oversold by remember { mutableStateOf(triggerCondition.value) }

                        Text("Aşırı Alım (Overbought)")
                        Slider(
                            value = overbought.toFloat(),
                            onValueChange = { 
                                overbought = it.toInt().toDouble()
                            },
                            valueRange = 50f..100f,
                            steps = 50
                        )
                        Text(String.format("%.0f", overbought))

                        Text("Aşırı Satım (Oversold)")
                        Slider(
                            value = oversold.toFloat(),
                            onValueChange = { 
                                oversold = it.toInt().toDouble()
                            },
                            valueRange = 0f..50f,
                            steps = 50
                        )
                        Text(String.format("%.0f", oversold))

                        // RSI sinyal değerlerini güncelle
                        triggerCondition = triggerCondition.copy(
                            value = oversold,
                            compareValue = overbought
                        )
                    }
                    "CCI" -> {
                        // CCI için özel sinyal değerleri
                        var upperLevel by remember { mutableStateOf(triggerCondition.compareValue ?: 100.0) }
                        var lowerLevel by remember { mutableStateOf(triggerCondition.value) }

                        Text("Üst Seviye")
                        Slider(
                            value = upperLevel.toFloat(),
                            onValueChange = { 
                                upperLevel = it.toInt().toDouble()
                            },
                            valueRange = 0f..200f,
                            steps = 200
                        )
                        Text(String.format("%.0f", upperLevel))

                        Text("Alt Seviye")
                        Slider(
                            value = lowerLevel.toFloat(),
                            onValueChange = { 
                                lowerLevel = it.toInt().toDouble()
                            },
                            valueRange = -200f..0f,
                            steps = 200
                        )
                        Text(String.format("%.0f", lowerLevel))

                        // CCI sinyal değerlerini güncelle
                        triggerCondition = triggerCondition.copy(
                            value = lowerLevel,
                            compareValue = upperLevel
                        )
                    }
                    "Stochastic" -> {
                        // Stochastic için özel sinyal değerleri
                        var upperLevel by remember { mutableStateOf(triggerCondition.compareValue ?: 80.0) }
                        var lowerLevel by remember { mutableStateOf(triggerCondition.value) }

                        Text("Üst Seviye")
                        Slider(
                            value = upperLevel.toFloat(),
                            onValueChange = { 
                                upperLevel = it.toInt().toDouble()
                            },
                            valueRange = 50f..100f,
                            steps = 50
                        )
                        Text(String.format("%.0f", upperLevel))

                        Text("Alt Seviye")
                        Slider(
                            value = lowerLevel.toFloat(),
                            onValueChange = { 
                                lowerLevel = it.toInt().toDouble()
                            },
                            valueRange = 0f..50f,
                            steps = 50
                        )
                        Text(String.format("%.0f", lowerLevel))

                        // Stochastic sinyal değerlerini güncelle
                        triggerCondition = triggerCondition.copy(
                            value = lowerLevel,
                            compareValue = upperLevel
                        )
                    }
                }
            }

            // Tetikleme koşulu seçimi
            if (!indicator.hasEditableSignalValues) {
                Text(
                    text = "Tetikleme Koşulu",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
                
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    TextField(
                        value = triggerCondition.type.name,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        TriggerType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name) },
                                onClick = {
                                    triggerCondition = triggerCondition.copy(type = type)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Kaydet butonu
            Button(
                onClick = {
                    onSave(indicator.copy(
                        parameters = parameters,
                        triggerCondition = triggerCondition
                    ))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Kaydet")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

private fun formatTriggerCondition(condition: TriggerCondition): String {
    return when (condition.type) {
        TriggerType.CROSSES_ABOVE -> "↗ ${condition.value}"
        TriggerType.CROSSES_BELOW -> "↘ ${condition.value}"
        TriggerType.GREATER_THAN -> "> ${condition.value}"
        TriggerType.LESS_THAN -> "< ${condition.value}"
        TriggerType.EQUALS -> "= ${condition.value}"
        TriggerType.BETWEEN -> "${condition.value} - ${condition.compareValue}"
    }
} 