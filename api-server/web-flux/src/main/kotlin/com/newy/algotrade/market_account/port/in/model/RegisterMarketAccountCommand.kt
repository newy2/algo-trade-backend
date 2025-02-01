package com.newy.algotrade.market_account.port.`in`.model

import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.market_account.domain.MarketAccount

data class RegisterMarketAccountCommand(
    val userId: Long,
    val displayName: String,
    val marketCode: String,
    val appKey: String,
    val appSecret: String,
) {
    fun toDomainModel() = MarketAccount(
        userId = userId,
        displayName = displayName,
        privateApiInfo = MarketAccount.PrivateApiInfo(
            marketCode = MarketCode.valueOf(marketCode),
            appKey = appKey,
            appSecret = appSecret
        )
    )
}