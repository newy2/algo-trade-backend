package com.newy.algotrade.market_account.port.out

import com.newy.algotrade.auth.domain.PrivateApiInfo
import com.newy.algotrade.common.consts.MarketCode

fun interface ValidMarketAccountOutPort {
    suspend fun validMarketAccount(marketCode: MarketCode, privateApiInfo: PrivateApiInfo): Boolean
}