package com.newy.algotrade.domain.market_account

data class SetMarketAccount(
    val userId: Long,
    val marketServer: MarketServer,
    val displayName: String,
    val appKey: String,
    val appSecret: String,
)