package com.newy.algotrade.coroutine_based_application.market_account.port.`in`.model

import com.newy.algotrade.domain.common.consts.Market

data class SetMarketAccountCommand(
    val market: Market,
    val isProduction: Boolean,
    val displayName: String,
    val appKey: String,
    val appSecret: String
)