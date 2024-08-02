package com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`

import com.newy.algotrade.domain.price.ProductPriceKey

interface RunStrategyUseCase {
    suspend fun runStrategy(productPriceKey: ProductPriceKey)
}