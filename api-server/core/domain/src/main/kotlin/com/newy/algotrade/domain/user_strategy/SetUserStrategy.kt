package com.newy.algotrade.domain.user_strategy

import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.consts.ProductCategory
import com.newy.algotrade.domain.common.consts.ProductType

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