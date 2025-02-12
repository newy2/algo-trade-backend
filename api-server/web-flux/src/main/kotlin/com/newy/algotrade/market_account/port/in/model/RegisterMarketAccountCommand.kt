package com.newy.algotrade.market_account.port.`in`.model

import com.newy.algotrade.auth.domain.PrivateApiInfo
import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.common.helper.SelfValidating
import com.newy.algotrade.market_account.domain.MarketAccount
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class RegisterMarketAccountCommand(
    @field:Min(1) val userId: Long,
    @field:NotBlank val displayName: String,
    @field:Pattern(regexp = "BY_BIT|LS_SEC") val marketCode: String,
    @field:NotBlank val appKey: String,
    @field:NotBlank val appSecret: String,
) : SelfValidating() {
    init {
        validate()
    }

    fun toDomainModel() = MarketAccount(
        userId = userId,
        displayName = displayName,
        marketCode = MarketCode.valueOf(marketCode),
        privateApiInfo = PrivateApiInfo(
            appKey = appKey,
            appSecret = appSecret
        )
    )
}