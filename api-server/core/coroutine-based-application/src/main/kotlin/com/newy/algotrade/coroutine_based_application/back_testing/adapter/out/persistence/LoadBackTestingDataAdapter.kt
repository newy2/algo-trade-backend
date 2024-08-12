package com.newy.algotrade.coroutine_based_application.back_testing.adapter.out.persistence

import com.newy.algotrade.coroutine_based_application.back_testing.port.out.FindBackTestingDataPort
import com.newy.algotrade.coroutine_based_application.product_price.port.out.OnReceivePollingPricePort
import com.newy.algotrade.coroutine_based_application.product_price.port.out.ProductPricePort
import com.newy.algotrade.coroutine_based_application.product_price.port.out.SubscribablePollingProductPricePort
import com.newy.algotrade.domain.back_testing.BackTestingDataKey
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.product_price.GetProductPriceHttpParam
import com.newy.algotrade.domain.product_price.ProductPriceKey

class LoadBackTestingDataAdapter(
    private val backTestingDataKey: BackTestingDataKey,
    private val backTestingDataPort: FindBackTestingDataPort,
    private val onReceivePollingPricePort: OnReceivePollingPricePort
) : ProductPricePort, SubscribablePollingProductPricePort {
    private lateinit var iterator: Iterator<ProductPrice>

    override suspend fun fetchProductPrices(param: GetProductPriceHttpParam): List<ProductPrice> {
        iterator = backTestingDataPort.findBackTestingData(backTestingDataKey).iterator()

        return (0 until param.limit).map {
            iterator.next()
        }
    }

    suspend fun await() {
        for (each in iterator) {
            onReceivePollingPricePort.onReceivePrice(backTestingDataKey.productPriceKey, listOf(each))
        }
    }

    override fun subscribe(key: ProductPriceKey) {
        // Do nothing
    }

    override fun unSubscribe(key: ProductPriceKey) {
        // Do nothing
    }
}