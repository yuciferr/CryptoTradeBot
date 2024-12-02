package com.example.cryptotradebot.di

import com.example.cryptotradebot.data.remote.BacktestApi
import com.example.cryptotradebot.data.remote.BinanceApi
import com.example.cryptotradebot.data.remote.LiveTradeApi
import com.example.cryptotradebot.data.remote.TradeWebSocketService
import com.example.cryptotradebot.data.repository.TradeRepositoryImpl
import com.example.cryptotradebot.domain.repository.TradeRepository
import com.example.cryptotradebot.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    @Named("binanceClient")
    fun provideBinanceHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("X-MBX-APIKEY", Constants.API_KEY)
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("tradeClient")
    fun provideTradeHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("wsClient")
    fun provideWebSocketClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .pingInterval(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    @Named("binanceRetrofit")
    fun provideBinanceRetrofit(@Named("binanceClient") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("tradeRetrofit")
    fun provideTradeRetrofit(@Named("tradeClient") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://localhost:8000")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideBinanceApi(@Named("binanceRetrofit") retrofit: Retrofit): BinanceApi {
        return retrofit.create(BinanceApi::class.java)
    }

    @Provides
    @Singleton
    fun provideBacktestApi(@Named("tradeRetrofit") retrofit: Retrofit): BacktestApi {
        return retrofit.create(BacktestApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLiveTradeApi(@Named("tradeRetrofit") retrofit: Retrofit): LiveTradeApi {
        return retrofit.create(LiveTradeApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTradeWebSocketService(@Named("wsClient") okHttpClient: OkHttpClient): TradeWebSocketService {
        return TradeWebSocketService(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideTradeRepository(
        backtestApi: BacktestApi,
        liveTradeApi: LiveTradeApi,
        webSocketService: TradeWebSocketService
    ): TradeRepository {
        return TradeRepositoryImpl(backtestApi, liveTradeApi, webSocketService)
    }
} 