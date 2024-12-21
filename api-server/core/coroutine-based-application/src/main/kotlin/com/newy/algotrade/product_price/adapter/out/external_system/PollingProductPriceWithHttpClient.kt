package com.newy.algotrade.product_price.adapter.out.external_system

import com.newy.algotrade.common.coroutine.PollingCallback
import com.newy.algotrade.common.coroutine.PollingJob
import com.newy.algotrade.common.domain.extension.ProductPrice
import com.newy.algotrade.product_price.domain.GetProductPriceHttpParam
import com.newy.algotrade.product_price.domain.ProductPriceKey
import com.newy.algotrade.product_price.port.out.ProductPricePort
import kotlinx.coroutines.Dispatchers
import java.time.OffsetDateTime
import kotlin.coroutines.CoroutineContext

open class PollingProductPriceWithHttpClient(
    private val loader: ProductPricePort,
    delayMillis: Long,
    @com.newy.algotrade.common.domain.annotation.ForTesting coroutineContext: CoroutineContext = Dispatchers.IO,
    pollingCallback: PollingCallback<ProductPriceKey, List<ProductPrice>>,
) : PollingJob<ProductPriceKey, List<ProductPrice>>(delayMillis, coroutineContext, pollingCallback) {
    override suspend fun eachProcess(key: ProductPriceKey): List<ProductPrice> {
        return loader.fetchProductPrices(
            GetProductPriceHttpParam(
                key,
                endTime(),
                limit()
            )
        )
    }

    open fun limit(): Int = 2
    open fun endTime(): OffsetDateTime = OffsetDateTime.now()
}