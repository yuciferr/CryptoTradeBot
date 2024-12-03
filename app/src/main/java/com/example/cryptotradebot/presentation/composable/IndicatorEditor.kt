package com.example.cryptotradebot.presentation.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cryptotradebot.R
import com.example.cryptotradebot.domain.model.Indicator
import com.example.cryptotradebot.domain.model.IndicatorList
import com.example.cryptotradebot.domain.model.TriggerCondition
import com.example.cryptotradebot.domain.model.TriggerType

private val activeIndicators = setOf(
    "RSI",
    "MACD",
    "Bollinger Bands",
    "CCI",
    "SuperTrend",
    "ADX"
)

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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.indicator_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { showIndicatorSelector = true }) {
                Icon(Icons.Default.Add, stringResource(R.string.indicator_add))
            }
        }

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
                                Icon(Icons.Default.Edit, stringResource(R.string.indicator_edit))
                            }
                            IconButton(onClick = { onRemoveIndicator(indicator) }) {
                                Icon(Icons.Default.Delete, stringResource(R.string.indicator_delete))
                            }
                        }
                    }

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

                    Text(
                        text = stringResource(R.string.indicator_signal_format, formatTriggerCondition(indicator.triggerCondition)),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }

    if (showIndicatorSelector) {
        ModalBottomSheet(onDismissRequest = { showIndicatorSelector = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.indicator_select_title),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                val categories = listOf(
                    R.string.indicator_category_trend,
                    R.string.indicator_category_momentum,
                    R.string.indicator_category_volume,
                    R.string.indicator_category_volatility
                )

                categories.forEach { categoryResId ->
                    val category = stringResource(categoryResId)
                    Text(
                        text = category,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    IndicatorList.availableIndicators
                        .filter { it.category == category }
                        .forEach { indicator ->
                            val isActive = activeIndicators.contains(indicator.name)
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .then(
                                        if (isActive) {
                                            Modifier.clickable {
                                                onAddIndicator(indicator)
                                                showIndicatorSelector = false
                                            }
                                        } else {
                                            Modifier
                                        }
                                    ),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = indicator.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = if (isActive) {
                                            MaterialTheme.colorScheme.onSurface
                                        } else {
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        }
                                    )
                                    if (!isActive) {
                                        Text(
                                            text = "(In progress)",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            }
                        }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

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
                text = stringResource(R.string.indicator_parameters_title, indicator.name),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

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
                        stringResource(R.string.indicator_parameter_decimal_format, param.value)
                    } else {
                        stringResource(R.string.indicator_parameter_integer_format, param.value)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            if (indicator.hasEditableSignalValues) {
                Text(
                    text = stringResource(R.string.indicator_signal_values),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                when (indicator.name) {
                    "RSI" -> {
                        var overbought by remember { mutableStateOf(triggerCondition.compareValue ?: 70.0) }
                        var oversold by remember { mutableStateOf(triggerCondition.value) }

                        Text(stringResource(R.string.indicator_rsi_overbought))
                        Slider(
                            value = overbought.toFloat(),
                            onValueChange = { 
                                overbought = it.toInt().toDouble()
                            },
                            valueRange = 50f..100f,
                            steps = 50
                        )
                        Text(stringResource(R.string.indicator_parameter_integer_format, overbought))

                        Text(stringResource(R.string.indicator_rsi_oversold))
                        Slider(
                            value = oversold.toFloat(),
                            onValueChange = { 
                                oversold = it.toInt().toDouble()
                            },
                            valueRange = 0f..50f,
                            steps = 50
                        )
                        Text(stringResource(R.string.indicator_parameter_integer_format, oversold))

                        triggerCondition = triggerCondition.copy(
                            value = oversold,
                            compareValue = overbought
                        )
                    }
                    "CCI" -> {
                        var upperLevel by remember { mutableStateOf(triggerCondition.compareValue ?: 100.0) }
                        var lowerLevel by remember { mutableStateOf(triggerCondition.value) }

                        Text(stringResource(R.string.indicator_cci_upper))
                        Slider(
                            value = upperLevel.toFloat(),
                            onValueChange = { 
                                upperLevel = it.toInt().toDouble()
                            },
                            valueRange = 0f..200f,
                            steps = 200
                        )
                        Text(stringResource(R.string.indicator_parameter_integer_format, upperLevel))

                        Text(stringResource(R.string.indicator_cci_lower))
                        Slider(
                            value = lowerLevel.toFloat(),
                            onValueChange = { 
                                lowerLevel = it.toInt().toDouble()
                            },
                            valueRange = -200f..0f,
                            steps = 200
                        )
                        Text(stringResource(R.string.indicator_parameter_integer_format, lowerLevel))

                        triggerCondition = triggerCondition.copy(
                            value = lowerLevel,
                            compareValue = upperLevel
                        )
                    }
                    "Stochastic" -> {
                        var upperLevel by remember { mutableStateOf(triggerCondition.compareValue ?: 80.0) }
                        var lowerLevel by remember { mutableStateOf(triggerCondition.value) }

                        Text(stringResource(R.string.indicator_stoch_upper))
                        Slider(
                            value = upperLevel.toFloat(),
                            onValueChange = { 
                                upperLevel = it.toInt().toDouble()
                            },
                            valueRange = 50f..100f,
                            steps = 50
                        )
                        Text(stringResource(R.string.indicator_parameter_integer_format, upperLevel))

                        Text(stringResource(R.string.indicator_stoch_lower))
                        Slider(
                            value = lowerLevel.toFloat(),
                            onValueChange = { 
                                lowerLevel = it.toInt().toDouble()
                            },
                            valueRange = 0f..50f,
                            steps = 50
                        )
                        Text(stringResource(R.string.indicator_parameter_integer_format, lowerLevel))

                        triggerCondition = triggerCondition.copy(
                            value = lowerLevel,
                            compareValue = upperLevel
                        )
                    }
                }
            }

            if (!indicator.hasEditableSignalValues) {
                Text(
                    text = stringResource(R.string.indicator_trigger_condition),
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
                Text(stringResource(R.string.indicator_save))
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