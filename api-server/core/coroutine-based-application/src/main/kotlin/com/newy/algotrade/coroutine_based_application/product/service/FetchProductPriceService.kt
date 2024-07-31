package com.newy.algotrade.coroutine_based_application.product.service

import com.newy.algotrade.coroutine_based_application.product.port.`in`.FetchProductPriceQuery
import com.newy.algotrade.coroutine_based_application.product.port.out.ProductPriceQueryPort
import com.newy.algotrade.coroutine_based_application.product.port.out.SubscribePollingProductPricePort
import com.newy.algotrade.coroutine_based_application.product.port.out.model.GetProductPriceParam
import com.newy.algotrade.domain.chart.DEFAULT_CANDLE_SIZE
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import java.time.OffsetDateTime

open class FetchProductPriceService(
    private val productPricePort: ProductPriceQueryPort,
    private val pollingProductPricePort: SubscribePollingProductPricePort,
    private val initDataSize: Int = DEFAULT_CANDLE_SIZE
) : FetchProductPriceQuery {
    override suspend fun fetchInitProductPrices(productPriceKey: ProductPriceKey) =
        productPricePort.getProductPrices(
            GetProductPriceParam(
                productPriceKey,
                OffsetDateTime.now(),
                initDataSize,
            )
        )

    override suspend fun requestPollingProductPrice(productPriceKey: ProductPriceKey) {
        pollingProductPricePort.subscribe(productPriceKey)
    }
}