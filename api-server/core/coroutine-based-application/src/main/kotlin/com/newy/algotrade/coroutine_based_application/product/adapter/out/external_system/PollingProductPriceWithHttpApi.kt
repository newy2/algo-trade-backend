package com.newy.algotrade.coroutine_based_application.product.adapter.out.external_system

import com.newy.algotrade.coroutine_based_application.common.coroutine.PollingJob
import com.newy.algotrade.coroutine_based_application.product.port.out.PollingProductPricePort
import com.newy.algotrade.coroutine_based_application.product.port.out.ProductPriceQueryPort
import com.newy.algotrade.coroutine_based_application.product.port.out.model.GetProductPriceParam
import com.newy.algotrade.domain.common.annotation.ForTesting
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import kotlinx.coroutines.Dispatchers
import java.time.OffsetDateTime
import kotlin.coroutines.CoroutineContext

open class PollingProductPriceWithHttpApi(
    private val loader: ProductPriceQueryPort,
    delayMillis: Long,
    @ForTesting coroutineContext: CoroutineContext = Dispatchers.IO,
) : PollingProductPricePort,
    PollingJob<ProductPriceKey, List<ProductPrice>>(delayMillis, coroutineContext) {
    override suspend fun eachProcess(key: ProductPriceKey): List<ProductPrice> {
        return loader.getProductPrices(
            GetProductPriceParam(
                key,
                endTime(),
                limit()
            )
        )
    }

    open fun limit(): Int = 2
    open fun endTime(): OffsetDateTime = OffsetDateTime.now()
}