package com.newy.algotrade.market_account.port.out

import com.newy.algotrade.market_account.domain.MarketAccount

fun interface ValidMarketAccountOutPort {
    suspend fun validMarketAccount(privateApiInfo: MarketAccount.PrivateApiInfo): Boolean
}