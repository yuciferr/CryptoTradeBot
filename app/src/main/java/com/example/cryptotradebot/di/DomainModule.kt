package com.example.cryptotradebot.di

import com.example.cryptotradebot.domain.repository.StrategyRepository
import com.example.cryptotradebot.domain.use_case.strategy.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    @Singleton
    fun provideGetAllStrategiesUseCase(repository: StrategyRepository): GetAllStrategiesUseCase {
        return GetAllStrategiesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSaveStrategyUseCase(repository: StrategyRepository): SaveStrategyUseCase {
        return SaveStrategyUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideUpdateStrategySettingsUseCase(repository: StrategyRepository): UpdateStrategySettingsUseCase {
        return UpdateStrategySettingsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideToggleStrategyUseCase(repository: StrategyRepository): ToggleStrategyUseCase {
        return ToggleStrategyUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteStrategyUseCase(repository: StrategyRepository): DeleteStrategyUseCase {
        return DeleteStrategyUseCase(repository)
    }
} 