package com.newy.algotrade.coroutine_based_application.market_account.port.out

import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.market_account.MarketAccount
import com.newy.algotrade.domain.market_account.MarketServer

interface MarketAccountPort :
    HasMarketAccountPort,
    GetMarketServerPort,
    SaveMarketAccountPort

fun interface HasMarketAccountPort {
    suspend fun hasMarketAccount(domainEntity: MarketAccount): Boolean
}

fun interface GetMarketServerPort {
    suspend fun getMarketServer(market: Market, isProductionServer: Boolean): MarketServer?
}

fun interface SaveMarketAccountPort {
    suspend fun saveMarketAccount(domainEntity: MarketAccount): MarketAccount
}