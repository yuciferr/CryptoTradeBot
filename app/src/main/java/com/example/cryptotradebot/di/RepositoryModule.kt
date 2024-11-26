package com.example.cryptotradebot.di

import com.example.cryptotradebot.data.repository.CandlestickRepositoryImpl
import com.example.cryptotradebot.domain.repository.CandlestickRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCandlestickRepository(
        candlestickRepositoryImpl: CandlestickRepositoryImpl
    ): CandlestickRepository
} 