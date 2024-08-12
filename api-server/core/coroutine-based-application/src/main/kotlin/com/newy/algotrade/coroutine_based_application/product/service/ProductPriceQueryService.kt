package com.newy.algotrade.coroutine_based_application.product.service

import com.newy.algotrade.coroutine_based_application.product.port.`in`.ProductPriceQuery
import com.newy.algotrade.coroutine_based_application.product.port.out.ProductPricePort
import com.newy.algotrade.coroutine_based_application.product.port.out.SubscribablePollingProductPricePort
import com.newy.algotrade.domain.chart.DEFAULT_CANDLE_SIZE
import com.newy.algotrade.domain.product.GetProductPriceHttpParam
import com.newy.algotrade.domain.product.ProductPriceKey
import java.time.OffsetDateTime

open class ProductPriceQueryService(
    private val productPricePort: ProductPricePort,
    private val pollingProductPricePort: SubscribablePollingProductPricePort,
    private val initDataSize: Int = DEFAULT_CANDLE_SIZE
) : ProductPriceQuery {
    override suspend fun getInitProductPrices(productPriceKey: ProductPriceKey) =
        // TODO? endTime, limit 도 파라미터로 받을까?
        productPricePort.fetchProductPrices(
            GetProductPriceHttpParam(
                productPriceKey = productPriceKey,
                endTime = OffsetDateTime.now(),
                limit = initDataSize,
            )
        )

    override fun requestPollingProductPrice(productPriceKey: ProductPriceKey) {
        pollingProductPricePort.subscribe(productPriceKey)
    }

    override fun requestUnPollingProductPrice(productPriceKey: ProductPriceKey) {
        pollingProductPricePort.unSubscribe(productPriceKey)
    }
}