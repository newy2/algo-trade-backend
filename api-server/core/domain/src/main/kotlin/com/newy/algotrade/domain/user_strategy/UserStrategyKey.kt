package com.newy.algotrade.domain.user_strategy

import com.newy.algotrade.domain.product_price.ProductPriceKey

data class UserStrategyKey(
    val userStrategyId: Long,
    val strategyClassName: String,
    val productPriceKey: ProductPriceKey,
)