package com.newy.algotrade.coroutine_based_application.market_account.port.`in`.model

import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.helper.SelfValidating
import jakarta.validation.constraints.NotBlank

data class SetMarketAccountCommand(
    val market: Market,
    val isProduction: Boolean,
    @field:NotBlank val displayName: String,
    @field:NotBlank val appKey: String,
    @field:NotBlank val appSecret: String
) : SelfValidating() {
    init {
        validate()
    }
}