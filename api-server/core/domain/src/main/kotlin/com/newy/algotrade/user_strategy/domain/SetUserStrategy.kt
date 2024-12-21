package com.newy.algotrade.user_strategy.domain

import com.newy.algotrade.chart.domain.Candle
import com.newy.algotrade.common.domain.consts.ProductCategory
import com.newy.algotrade.common.domain.consts.ProductType

data class SetUserStrategy(
    val setUserStrategyKey: SetUserStrategyKey,
    val productCategory: ProductCategory,
    val timeFrame: Candle.TimeFrame,
)

data class SetUserStrategyKey(
    val marketServerAccountId: Long,
    val strategyClassName: String,
    val productType: ProductType,
)