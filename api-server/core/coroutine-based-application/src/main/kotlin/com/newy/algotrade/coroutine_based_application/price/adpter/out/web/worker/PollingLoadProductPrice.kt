package com.newy.algotrade.coroutine_based_application.price.adpter.out.web.worker

import com.newy.algotrade.coroutine_based_application.common.coroutine.PollingJob
import com.newy.algotrade.coroutine_based_application.price2.port.out.GetProductPricePort
import com.newy.algotrade.coroutine_based_application.price2.port.out.model.GetProductPriceParam
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import java.time.OffsetDateTime
import kotlin.coroutines.CoroutineContext

// TODO Refector name -> PollingLoadProductPriceHttpApi
open class PollingLoadProductPrice(
    private val loader: GetProductPricePort,
    delayMillis: Long,
    coroutineContext: CoroutineContext,
) : PollingJob<ProductPriceKey, List<ProductPrice>>(delayMillis, coroutineContext) {
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