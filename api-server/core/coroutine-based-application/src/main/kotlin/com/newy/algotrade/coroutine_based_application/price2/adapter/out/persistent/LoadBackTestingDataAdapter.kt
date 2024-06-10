package com.newy.algotrade.coroutine_based_application.price2.adapter.out.persistent

import com.newy.algotrade.coroutine_based_application.price2.port.`in`.model.BackTestingDataKey
import com.newy.algotrade.coroutine_based_application.price2.port.out.GetBackTestingDataPort
import com.newy.algotrade.coroutine_based_application.price2.port.out.GetProductPricePort
import com.newy.algotrade.coroutine_based_application.price2.port.out.OnReceivePollingPricePort
import com.newy.algotrade.coroutine_based_application.price2.port.out.SubscribePollingProductPricePort
import com.newy.algotrade.coroutine_based_application.price2.port.out.model.GetProductPriceParam
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