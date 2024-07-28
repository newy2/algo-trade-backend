package com.newy.algotrade.coroutine_based_application.market_account.port.out

import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.market_account.MarketAccount
import com.newy.algotrade.domain.market_account.MarketServer

interface MarketAccountPort : MarketAccountQueryPort, MarketAccountCommandPort

interface MarketAccountQueryPort {
    suspend fun hasMarketAccount(domainEntity: MarketAccount): Boolean
    suspend fun getMarketServer(market: Market, isProductionServer: Boolean): MarketServer?
}

interface MarketAccountCommandPort {
    suspend fun saveMarketAccount(domainEntity: MarketAccount): Boolean
}