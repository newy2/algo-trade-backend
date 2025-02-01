package com.newy.algotrade.market_account.port.`in`.model

import com.newy.algotrade.market_account.domain.MarketAccount

data class RegisterMarketAccountCommand(
    val userId: Long,
    val displayName: String,
    val marketCode: String,
    val appKey: String,
    val appSecret: String,
) {
    fun toDomainModel() = MarketAccount(
        key = MarketAccount.Key(
            userId = userId,
            displayName = displayName
        ),
        privateApiInfo = MarketAccount.PrivateApiInfo(
            marketCode = marketCode,
            appKey = appKey,
            appSecret = appSecret
        )
    )
}