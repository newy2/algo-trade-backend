package com.newy.algotrade.setting.domain

import com.newy.algotrade.common.consts.MarketCode

data class MarketAccount(
    val id: Long,
    val marketCode: MarketCode,
    val marketName: String,
    val displayName: String,
    val appKey: String,
    val appSecret: String,
)