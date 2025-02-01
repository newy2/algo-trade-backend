package com.newy.algotrade.market_account.domain

data class MarketAccount(
    val key: Key,
    val privateApiInfo: PrivateApiInfo,
) {
    data class Key(
        val userId: Long,
        val displayName: String,
    )

    data class PrivateApiInfo(
        val marketCode: String,
        val appKey: String,
        val appSecret: String,
    )
}