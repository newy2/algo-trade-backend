package com.newy.algotrade.coroutine_based_application.product.port.`in`.candle

import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

interface SetCandlesUseCase {
    suspend fun setCandles(productPriceKey: ProductPriceKey)
}
