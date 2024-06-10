package com.newy.algotrade.coroutine_based_application.product.adapter.out.persistent

import com.newy.algotrade.coroutine_based_application.product.port.`in`.model.BackTestingDataKey
import com.newy.algotrade.coroutine_based_application.product.port.out.GetBackTestingDataPort
import com.newy.algotrade.coroutine_based_application.product.port.out.GetProductPricePort
import com.newy.algotrade.coroutine_based_application.product.port.out.OnReceivePollingPricePort
import com.newy.algotrade.coroutine_based_application.product.port.out.SubscribePollingProductPricePort
import com.newy.algotrade.coroutine_based_application.product.port.out.model.GetProductPriceParam
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

class LoadBackTestingDataAdapter(
    private val backTestingDataKey: BackTestingDataKey,
    private val backTestingDataPort: GetBackTestingDataPort,
    private val onReceivePollingPricePort: OnReceivePollingPricePort
) : GetProductPricePort, SubscribePollingProductPricePort {
    private lateinit var iterator: Iterator<ProductPrice>

    override suspend fun getProductPrices(param: GetProductPriceParam): List<ProductPrice> {
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

    override suspend fun subscribe(key: ProductPriceKey) {
        // Do nothing
    }
}