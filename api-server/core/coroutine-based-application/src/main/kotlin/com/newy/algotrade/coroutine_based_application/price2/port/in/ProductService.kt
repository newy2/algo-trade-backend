package com.newy.algotrade.coroutine_based_application.price2.port.`in`

import com.newy.algotrade.coroutine_based_application.price2.port.out.CandlesPort
import com.newy.algotrade.coroutine_based_application.price2.port.out.GetProductPricePort
import com.newy.algotrade.coroutine_based_application.price2.port.out.model.GetProductPriceParam
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import java.time.OffsetDateTime

class ProductService(
    private val getProductPricePort: GetProductPricePort,
    private val candlesPort: CandlesPort,
    private val initDataSize: Int = 400
) {
    // TODO registerBulkProduct

    suspend fun registerProduct(key: Key) {
        val (_, productPriceKey) = key

        if (isEmptyCandles(productPriceKey)) {
            setCandles(productPriceKey)
        }
    }

    private fun isEmptyCandles(productPriceKey: ProductPriceKey) =
        candlesPort.getCandles(productPriceKey).size == 0

    private suspend fun setCandles(productPriceKey: ProductPriceKey) {
        getProductPricePort.getProductPrices(
            GetProductPriceParam(
                productPriceKey,
                OffsetDateTime.now(),
                initDataSize,
            )
        ).let {
            candlesPort.setCandles(productPriceKey, it)
        }
    }

    data class Key(
        val userStrategyId: String,
        val productPriceKey: ProductPriceKey,
    )
}