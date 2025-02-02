package com.newy.algotrade.market_account.adapter.`in`.web.model

import com.newy.algotrade.market_account.port.`in`.model.RegisterMarketAccountCommand

data class RegisterMarketAccountRequest(
    val displayName: String,
    val marketCode: String,
    val appKey: String,
    val appSecret: String,
) {
    fun toInPortModel(userId: Long) = RegisterMarketAccountCommand(
        userId = userId,
        displayName = displayName,
        marketCode = marketCode,
        appKey = appKey,
        appSecret = appSecret,
    )
}