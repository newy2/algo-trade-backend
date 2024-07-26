package com.newy.algotrade.coroutine_based_application.product.service

import com.newy.algotrade.coroutine_based_application.product.port.`in`.AddCandlesUseCase
import com.newy.algotrade.coroutine_based_application.product.port.out.AddCandlePort
import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

open class AddCandlesService(
    private val candlePort: AddCandlePort
) : AddCandlesUseCase {
    override fun addCandles(productPriceKey: ProductPriceKey, candleList: List<ProductPrice>): Candles {
        return candlePort.addCandles(productPriceKey, candleList)
    }
}