package com.newy.algotrade.coroutine_based_application.product.port.`in`

import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.ProductPriceKey

// TODO Add, Set Interface 를 없애야 할까?

interface CandlesUseCase : SetCandlesUseCase, AddCandlesUseCase, RemoveCandlesUseCase

interface AddCandlesUseCase {
    fun addCandles(productPriceKey: ProductPriceKey, candleList: List<ProductPrice>): Candles
}

interface SetCandlesUseCase {
    suspend fun setCandles(productPriceKey: ProductPriceKey): Candles
}

interface RemoveCandlesUseCase {
    fun removeCandles(productPriceKey: ProductPriceKey)
}