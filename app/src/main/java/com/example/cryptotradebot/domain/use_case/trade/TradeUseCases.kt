package com.example.cryptotradebot.domain.use_case.trade

data class TradeUseCases(
    val runBacktest: RunBacktestUseCase,
    val startLiveTrade: StartLiveTradeUseCase,
    val stopLiveTrade: StopLiveTradeUseCase,
    val getLiveTradeStatus: GetLiveTradeStatusUseCase,
    val getTradeUpdates: GetTradeUpdatesUseCase,
    val connectToTradeUpdates: ConnectToTradeUpdatesUseCase,
    val disconnectFromTradeUpdates: DisconnectFromTradeUpdatesUseCase,
    val sendTradeMessage: SendTradeMessageUseCase
) 