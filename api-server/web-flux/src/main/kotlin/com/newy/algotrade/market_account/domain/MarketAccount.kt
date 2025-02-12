package com.newy.algotrade.market_account.domain

import com.newy.algotrade.auth.domain.PrivateApiInfo
import com.newy.algotrade.common.consts.MarketCode

data class MarketAccount(
    val userId: Long,
    val displayName: String,
    val marketCode: MarketCode,
    val privateApiInfo: PrivateApiInfo,
)