package com.newy.algotrade.market_account.adapter.`in`.web.model

import com.newy.algotrade.common.consts.GlobalEnv
import com.newy.algotrade.common.consts.Market
import com.newy.algotrade.common.helper.SelfValidating
import com.newy.algotrade.market_account.port.`in`.model.SetMarketAccountCommand
import jakarta.validation.constraints.Pattern

data class SetMarketAccountRequest(
    @field:Pattern(regexp = "LS_SEC|BY_BIT") val market: String,
    val isProduction: Boolean,
    val displayName: String,
    val appKey: String,
    val appSecret: String
) : SelfValidating() {
    init {
        validate()
    }

    fun toIncomingPortModel() =
        SetMarketAccountCommand(
            userId = GlobalEnv.ADMIN_USER_ID,
            market = Market.valueOf(market),
            isProduction = isProduction,
            displayName = displayName,
            appKey = appKey,
            appSecret = appSecret,
        )
}