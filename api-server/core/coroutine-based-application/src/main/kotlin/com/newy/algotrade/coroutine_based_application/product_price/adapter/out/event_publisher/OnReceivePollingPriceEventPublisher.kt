package com.newy.algotrade.coroutine_based_application.product_price.adapter.out.event_publisher

import com.newy.algotrade.coroutine_based_application.common.coroutine.EventBus
import com.newy.algotrade.coroutine_based_application.common.event.ReceivePollingPriceEvent
import com.newy.algotrade.coroutine_based_application.product_price.port.out.OnReceivePollingPricePort
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.product_price.ProductPriceKey

open class OnReceivePollingPriceEventPublisher(
    private val eventBus: EventBus<ReceivePollingPriceEvent>
) : OnReceivePollingPricePort {
    override suspend fun onReceivePrice(productPriceKey: ProductPriceKey, productPriceList: List<ProductPrice>) {
        eventBus.publishEvent(
            ReceivePollingPriceEvent(
                productPriceKey = productPriceKey,
                productPriceList = productPriceList,
            )
        )
    }
}