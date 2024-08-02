package com.newy.algotrade.domain.user_strategy

import com.newy.algotrade.domain.product.ProductPriceKey

data class UserStrategyKey(
    val userStrategyId: String,
    val strategyClassName: String,
    val productPriceKey: ProductPriceKey,
)