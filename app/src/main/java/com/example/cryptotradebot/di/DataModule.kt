package com.example.cryptotradebot.di

import android.app.Application
import androidx.room.Room
import com.example.cryptotradebot.data.local.CryptoDatabase
import com.example.cryptotradebot.data.repository.CandlestickRepositoryImpl
import com.example.cryptotradebot.data.repository.StrategyRepositoryImpl
import com.example.cryptotradebot.domain.repository.CandlestickRepository
import com.example.cryptotradebot.domain.repository.StrategyRepository
import dagger.Module
import dagger.Provides
import dagger.Binds
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    companion object {
        @Provides
        @Singleton
        fun provideCryptoDatabase(app: Application): CryptoDatabase {
            return Room.databaseBuilder(
                app,
                CryptoDatabase::class.java,
                CryptoDatabase.DATABASE_NAME
            ).build()
        }

        @Provides
        @Singleton
        fun provideStrategyDao(db: CryptoDatabase) = db.strategyDao
    }

    @Binds
    @Singleton
    abstract fun bindStrategyRepository(impl: StrategyRepositoryImpl): StrategyRepository

    @Binds
    @Singleton
    abstract fun bindCandlestickRepository(
        impl: CandlestickRepositoryImpl
    ): CandlestickRepository
} 