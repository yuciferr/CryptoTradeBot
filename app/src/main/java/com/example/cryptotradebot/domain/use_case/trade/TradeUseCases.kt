package com.example.cryptotradebot.domain.use_case.trade

import javax.inject.Inject

data class TradeUseCases @Inject constructor(
    val runBacktest: RunBacktestUseCase,
    val startLiveTrade: StartLiveTradeUseCase,
    val getLiveTradeStatus: GetLiveTradeStatusUseCase,
    val stopLiveTrade: StopLiveTradeUseCase,
    val connectToTradeSignals: ConnectToTradeSignalsUseCase,
    val disconnectFromTradeSignals: DisconnectFromTradeSignalsUseCase,
    val getTradeSignals: GetTradeSignalsUseCase
)