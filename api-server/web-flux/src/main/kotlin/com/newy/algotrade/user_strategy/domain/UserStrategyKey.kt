package com.newy.algotrade.user_strategy.domain

import com.newy.algotrade.product_price.domain.ProductPriceKey

data class UserStrategyKey(
    val userStrategyId: Long,
    val strategyClassName: String,
    val productPriceKey: ProductPriceKey,
)