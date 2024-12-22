package com.newy.algotrade.user_strategy.adapter.`in`.web.model

import com.newy.algotrade.chart.domain.Candle
import com.newy.algotrade.common.consts.ProductCategory
import com.newy.algotrade.common.consts.ProductType
import com.newy.algotrade.common.helper.SelfValidating
import com.newy.algotrade.user_strategy.port.`in`.model.SetUserStrategyCommand
import jakarta.validation.constraints.Pattern

data class SetUserStrategyRequest(
    val marketAccountId: Long,
    val strategyClassName: String,
    @field:Pattern(regexp = "USER_PICK|TOP_TRADING_VALUE") val productCategory: String,
    @field:Pattern(regexp = "SPOT|SPOT_MARGIN|FUTURE|PERPETUAL_FUTURE") val productType: String,
    val productCodes: List<String>,
    @field:Pattern(regexp = "M1|M3|M5|M15|M30|H1|D1") val timeFrame: String,
) : SelfValidating() {
    init {
        validate()
    }

    fun toIncomingPortModel() =
        SetUserStrategyCommand(
            marketAccountId,
            strategyClassName,
            ProductCategory.valueOf(productCategory),
            ProductType.valueOf(productType),
            productCodes,
            Candle.TimeFrame.valueOf(timeFrame),
        )
}