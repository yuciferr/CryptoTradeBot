package com.example.cryptotradebot.presentation.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cryptotradebot.domain.model.*

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
                        Text(
                            text = indicator.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
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
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = String.format("%.2f", param.value),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Tetikleme koşulu
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tetikleme",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            text = formatTriggerCondition(indicator.triggerCondition),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }

    // İndikatör seçim bottom sheet
    if (showIndicatorSelector) {
        ModalBottomSheet(
            onDismissRequest = { showIndicatorSelector = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "İndikatör Seçin",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                IndicatorList.availableIndicators.forEach { indicator ->
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
                Slider(
                    value = param.value.toFloat(),
                    onValueChange = { newValue ->
                        parameters = parameters.toMutableList().apply {
                            this[index] = param.copy(value = newValue.toDouble())
                        }
                    },
                    valueRange = param.minValue.toFloat()..param.maxValue.toFloat(),
                    steps = ((param.maxValue - param.minValue) / param.step).toInt() - 1
                )
                Text(
                    text = String.format("%.2f", param.value),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            // Tetikleme koşulu
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

            Spacer(modifier = Modifier.height(16.dp))

            // Değer
            TextField(
                value = triggerCondition.value.toString(),
                onValueChange = { newValue ->
                    newValue.toDoubleOrNull()?.let {
                        triggerCondition = triggerCondition.copy(value = it)
                    }
                },
                label = { Text("Değer") },
                modifier = Modifier.fillMaxWidth()
            )

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