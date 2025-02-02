package com.newy.algotrade.market_account.port.`in`.model

import com.newy.algotrade.auth.domain.PrivateApiInfo
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
        marketCode = MarketCode.valueOf(marketCode),
        privateApiInfo = PrivateApiInfo(
            appKey = this@RegisterMarketAccountCommand.appKey,
            appSecret = this@RegisterMarketAccountCommand.appSecret
        )
    )
}