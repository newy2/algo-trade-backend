package com.newy.algotrade.coroutine_based_application.product.port.`in`

import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.product.ProductPriceKey

interface CandlesUseCase : SetCandlesUseCase, AddCandlesUseCase, RemoveCandlesUseCase

fun interface AddCandlesUseCase {
    fun addCandles(productPriceKey: ProductPriceKey, candleList: List<ProductPrice>): Candles
}

fun interface SetCandlesUseCase {
    suspend fun setCandles(productPriceKey: ProductPriceKey): Candles
}

fun interface RemoveCandlesUseCase {
    fun removeCandles(productPriceKey: ProductPriceKey)
}