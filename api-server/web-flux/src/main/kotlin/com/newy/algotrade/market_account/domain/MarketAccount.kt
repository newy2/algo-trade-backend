package com.newy.algotrade.market_account.domain

import com.newy.algotrade.common.consts.MarketCode

data class MarketAccount(
    val userId: Long,
    val displayName: String,
    val privateApiInfo: PrivateApiInfo,
) {
    data class PrivateApiInfo(
        val marketCode: MarketCode,
        val appKey: String,
        val appSecret: String,
    )
}