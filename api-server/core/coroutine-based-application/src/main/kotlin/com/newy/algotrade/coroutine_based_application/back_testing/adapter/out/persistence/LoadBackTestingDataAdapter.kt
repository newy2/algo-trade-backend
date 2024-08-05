package com.newy.algotrade.coroutine_based_application.back_testing.adapter.out.persistence

import com.newy.algotrade.coroutine_based_application.back_testing.port.`in`.model.BackTestingDataKey
import com.newy.algotrade.coroutine_based_application.back_testing.port.out.GetBackTestingDataPort
import com.newy.algotrade.coroutine_based_application.product.port.out.OnReceivePollingPricePort
import com.newy.algotrade.coroutine_based_application.product.port.out.ProductPriceQueryPort
import com.newy.algotrade.coroutine_based_application.product.port.out.SubscribablePollingProductPricePort
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.product.GetProductPriceHttpParam
import com.newy.algotrade.domain.product.ProductPriceKey

class LoadBackTestingDataAdapter(
    private val backTestingDataKey: BackTestingDataKey,
    private val backTestingDataPort: GetBackTestingDataPort,
    private val onReceivePollingPricePort: OnReceivePollingPricePort
) : ProductPriceQueryPort, SubscribablePollingProductPricePort {
    private lateinit var iterator: Iterator<ProductPrice>

    override suspend fun getProductPrices(param: GetProductPriceHttpParam): List<ProductPrice> {
        iterator = backTestingDataPort.getBackTestingData(backTestingDataKey).iterator()

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