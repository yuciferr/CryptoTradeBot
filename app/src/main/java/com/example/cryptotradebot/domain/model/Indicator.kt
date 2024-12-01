package com.example.cryptotradebot.domain.model

data class Indicator(
    val name: String,
    val category: String,
    val parameters: List<Parameter>,
    val triggerCondition: TriggerCondition
) {
    val hasEditableSignalValues: Boolean
        get() = when (name) {
            "RSI", "CCI", "Stochastic" -> true
            else -> false
        }
}

data class Parameter(
    val name: String,
    val value: Double,
    val minValue: Double,
    val maxValue: Double,
    val step: Double = 1.0,
    val description: String? = null
)

data class TriggerCondition(
    val type: TriggerType,
    val value: Double,
    val compareValue: Double? = null
)

enum class TriggerType {
    CROSSES_ABOVE,
    CROSSES_BELOW,
    GREATER_THAN,
    LESS_THAN,
    EQUALS,
    BETWEEN
}