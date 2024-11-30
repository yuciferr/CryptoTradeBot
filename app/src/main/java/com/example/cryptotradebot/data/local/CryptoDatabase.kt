package com.example.cryptotradebot.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cryptotradebot.data.local.converter.IndicatorListConverter
import com.example.cryptotradebot.data.local.dao.StrategyDao
import com.example.cryptotradebot.data.local.entity.StrategyEntity

@Database(
    entities = [StrategyEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(IndicatorListConverter::class)
abstract class CryptoDatabase : RoomDatabase() {
    abstract val strategyDao: StrategyDao

    companion object {
        const val DATABASE_NAME = "crypto_db"
    }
} 