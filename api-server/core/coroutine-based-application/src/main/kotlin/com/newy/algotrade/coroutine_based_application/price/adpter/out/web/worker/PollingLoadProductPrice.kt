package com.newy.algotrade.coroutine_based_application.price.adpter.out.web.worker

import com.newy.algotrade.coroutine_based_application.common.coroutine.PollingJob
import com.newy.algotrade.coroutine_based_application.price.port.out.LoadProductPricePort
import com.newy.algotrade.coroutine_based_application.price.port.out.model.LoadProductPriceParam
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import java.time.OffsetDateTime
import kotlin.coroutines.CoroutineContext

open class PollingLoadProductPrice(
    private val loader: LoadProductPricePort,
    delayMillis: Long,
    coroutineContext: CoroutineContext,
    callback: suspend (Pair<ProductPriceKey, List<ProductPrice>>) -> Unit
) : PollingJob<ProductPriceKey, List<ProductPrice>>(delayMillis, coroutineContext, callback) {
    override suspend fun eachProcess(key: ProductPriceKey): List<ProductPrice> {
        return loader.productPrices(
            LoadProductPriceParam(
                key,
                endTime(),
                limit()
            )
        )
    }

    open fun limit(): Int = 2
    open fun endTime(): OffsetDateTime = OffsetDateTime.now()
}