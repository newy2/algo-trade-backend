package com.newy.algotrade.coroutine_based_application.price2.port.`in`.strategy

import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

interface RunStrategyUseCase {
    suspend fun runStrategy(productPriceKey: ProductPriceKey)
}