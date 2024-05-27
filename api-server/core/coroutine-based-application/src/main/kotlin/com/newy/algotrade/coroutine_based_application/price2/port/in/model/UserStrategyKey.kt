package com.newy.algotrade.coroutine_based_application.price2.port.`in`.model

import com.newy.algotrade.domain.chart.strategy.StrategyId
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

data class UserStrategyKey(
    val userStrategyId: String,
    val strategyId: StrategyId,
    val productPriceKey: ProductPriceKey,
)