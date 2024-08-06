package com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`

import com.newy.algotrade.domain.product.ProductPriceKey
import com.newy.algotrade.domain.run_strategy.RunStrategyResult

fun interface RunStrategyUseCase {
    suspend fun runStrategy(productPriceKey: ProductPriceKey): RunStrategyResult
}