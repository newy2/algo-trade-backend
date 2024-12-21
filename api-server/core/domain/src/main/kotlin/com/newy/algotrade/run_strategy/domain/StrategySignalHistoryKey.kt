package com.newy.algotrade.run_strategy.domain

import com.newy.algotrade.product_price.domain.ProductPriceKey

data class StrategySignalHistoryKey(
    val userStrategyId: Long,
    val productPriceKey: ProductPriceKey,
)