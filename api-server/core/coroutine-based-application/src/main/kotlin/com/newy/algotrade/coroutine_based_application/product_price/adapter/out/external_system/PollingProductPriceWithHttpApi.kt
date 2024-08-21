package com.newy.algotrade.coroutine_based_application.product_price.adapter.out.external_system

import com.newy.algotrade.coroutine_based_application.common.coroutine.PollingCallback
import com.newy.algotrade.coroutine_based_application.common.coroutine.PollingJob
import com.newy.algotrade.coroutine_based_application.product_price.port.out.PollingProductPricePort
import com.newy.algotrade.coroutine_based_application.product_price.port.out.ProductPricePort
import com.newy.algotrade.domain.common.annotation.ForTesting
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.product_price.GetProductPriceHttpParam
import com.newy.algotrade.domain.product_price.ProductPriceKey
import kotlinx.coroutines.Dispatchers
import java.time.OffsetDateTime
import kotlin.coroutines.CoroutineContext

open class PollingProductPriceWithHttpApi(
    loader: ProductPricePort,
    delayMillis: Long,
    @ForTesting coroutineContext: CoroutineContext = Dispatchers.IO,
    pollingCallback: PollingCallback<ProductPriceKey, List<ProductPrice>>,
    private val delegate: PollingFetchProductPriceJob = PollingFetchProductPriceJob(
        loader = loader,
        delayMillis = delayMillis,
        coroutineContext = coroutineContext,
        pollingCallback = pollingCallback,
    )
) : PollingProductPricePort {
    override suspend fun start() = delegate.start()

    override fun cancel() = delegate.cancel()

    override fun unSubscribe(key: ProductPriceKey) = delegate.unSubscribe(key)

    override fun subscribe(key: ProductPriceKey) = delegate.subscribe(key)
}

open class PollingFetchProductPriceJob(
    private val loader: ProductPricePort,
    delayMillis: Long,
    @ForTesting coroutineContext: CoroutineContext = Dispatchers.IO,
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