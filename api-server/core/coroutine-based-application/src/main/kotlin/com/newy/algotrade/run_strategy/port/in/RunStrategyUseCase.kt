package com.newy.algotrade.run_strategy.port.`in`

import com.newy.algotrade.product_price.domain.ProductPriceKey
import com.newy.algotrade.run_strategy.domain.RunStrategyResult

fun interface RunStrategyUseCase {
    suspend fun runStrategy(productPriceKey: ProductPriceKey): RunStrategyResult
}