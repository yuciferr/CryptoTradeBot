package com.example.cryptotradebot.di

import android.app.Application
import androidx.room.Room
import com.example.cryptotradebot.data.local.CryptoDatabase
import com.example.cryptotradebot.data.repository.StrategyRepositoryImpl
import com.example.cryptotradebot.domain.repository.StrategyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

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

    @Provides
    @Singleton
    fun provideStrategyRepository(impl: StrategyRepositoryImpl): StrategyRepository = impl
} 