package com.newy.algotrade.market_account.port.`in`.model

import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.helper.SelfValidating
import com.newy.algotrade.domain.market_account.MarketAccount
import com.newy.algotrade.domain.market_account.MarketServer
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class SetMarketAccountCommand(
    @field:Min(1) val userId: Long,
    val market: Market,
    val isProduction: Boolean,
    @field:NotBlank val displayName: String,
    @field:NotBlank val appKey: String,
    @field:NotBlank val appSecret: String
) : SelfValidating() {
    init {
        validate()
    }

    fun toDomainEntity(marketServer: MarketServer) = MarketAccount(
        userId = userId,
        marketServer = marketServer,
        displayName = displayName,
        appKey = appKey,
        appSecret = appSecret
    )
}