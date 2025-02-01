package com.newy.algotrade.market_account.port.out

import com.newy.algotrade.market_account.domain.MarketAccount

fun interface ExistsMarketAccountOutPort {
    suspend fun existsMarketAccount(key: MarketAccount.Key): Boolean
}