package com.newy.algotrade.market_account.port.out

import com.newy.algotrade.market_account.domain.MarketAccount

fun interface SaveMarketAccountOutPort {
    suspend fun saveMarketAccount(marketAccount: MarketAccount)
}