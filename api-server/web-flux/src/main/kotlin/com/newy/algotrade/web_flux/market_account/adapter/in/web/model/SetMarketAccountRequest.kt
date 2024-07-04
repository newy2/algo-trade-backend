package com.newy.algotrade.web_flux.market_account.adapter.`in`.web.model

import com.newy.algotrade.coroutine_based_application.market_account.application.port.`in`.model.SetMarketAccountCommand
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.helper.SelfValidating
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class SetMarketAccountRequest(
    @field:Pattern(regexp = "LS_SEC|BY_BIT") val market: String,
    val isProduction: Boolean,
    @field:NotBlank val displayName: String,
    @field:NotBlank val appKey: String,
    @field:NotBlank val appSecret: String
) : SelfValidating() {
    init {
        validate()
    }

    fun toDomainModel() =
        SetMarketAccountCommand(
            Market.valueOf(market),
            isProduction,
            displayName,
            appKey,
            appSecret,
        )
}