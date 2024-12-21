package com.newy.algotrade.market_account.port.out

import com.newy.algotrade.common.domain.consts.Market
import com.newy.algotrade.market_account.domain.MarketAccount
import com.newy.algotrade.market_account.domain.MarketServer

interface MarketAccountPort :
    ExistsMarketAccountPort,
    FindMarketServerPort,
    SaveMarketAccountPort

fun interface ExistsMarketAccountPort {
    suspend fun existsMarketAccount(domainEntity: MarketAccount): Boolean
}

fun interface FindMarketServerPort {
    suspend fun findMarketServer(market: Market, isProductionServer: Boolean): MarketServer?
}

fun interface SaveMarketAccountPort {
    suspend fun saveMarketAccount(domainEntity: MarketAccount): MarketAccount
}