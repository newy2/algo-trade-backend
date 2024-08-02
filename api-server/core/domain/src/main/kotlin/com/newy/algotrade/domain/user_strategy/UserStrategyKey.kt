package com.newy.algotrade.domain.user_strategy

import com.newy.algotrade.domain.price.ProductPriceKey

data class UserStrategyKey(
    val userStrategyId: String,
    val strategyClassName: String,
    val productPriceKey: ProductPriceKey,
)