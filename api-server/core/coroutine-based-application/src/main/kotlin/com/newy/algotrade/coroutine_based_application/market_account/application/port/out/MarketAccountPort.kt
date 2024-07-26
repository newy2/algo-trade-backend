package com.newy.algotrade.coroutine_based_application.market_account.application.port.out

import com.newy.algotrade.coroutine_based_application.market_account.application.port.`in`.model.SetMarketAccountCommand

interface MarketAccountPort : MarketAccountQueryPort, MarketAccountCommandPort

interface MarketAccountQueryPort {
    suspend fun hasMarketAccount(marketAccount: SetMarketAccountCommand): Boolean
}

interface MarketAccountCommandPort {
    suspend fun setMarketAccount(marketAccount: SetMarketAccountCommand): Boolean
}