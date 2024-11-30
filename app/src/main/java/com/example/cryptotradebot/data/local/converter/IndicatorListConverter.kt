package com.example.cryptotradebot.data.local.converter

import androidx.room.TypeConverter
import com.example.cryptotradebot.domain.model.Indicator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class IndicatorListConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromIndicatorList(value: List<Indicator>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toIndicatorList(value: String): List<Indicator> {
        val listType = object : TypeToken<List<Indicator>>() {}.type
        return gson.fromJson(value, listType)
    }
} 