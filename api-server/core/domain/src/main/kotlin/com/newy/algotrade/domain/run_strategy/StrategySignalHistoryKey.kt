package com.newy.algotrade.domain.run_strategy

import com.newy.algotrade.domain.product_price.ProductPriceKey

data class StrategySignalHistoryKey(
    val userStrategyId: Long,
    val productPriceKey: ProductPriceKey,
)