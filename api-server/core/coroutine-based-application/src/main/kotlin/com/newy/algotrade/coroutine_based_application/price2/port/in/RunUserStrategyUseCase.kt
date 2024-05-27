package com.newy.algotrade.coroutine_based_application.price2.port.`in`

import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

interface RunUserStrategyUseCase {
    fun run(productPriceKey: ProductPriceKey)
}
