package com.newy.algotrade.setting.port.out

import com.newy.algotrade.setting.domain.MarketAccount

fun interface GetMarketAccountsOutPort {
    suspend fun getMarketAccounts(userId: Long): List<MarketAccount>
}