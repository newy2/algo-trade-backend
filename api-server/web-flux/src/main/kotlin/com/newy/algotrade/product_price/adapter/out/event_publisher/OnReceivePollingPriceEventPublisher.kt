package com.newy.algotrade.product_price.adapter.out.event_publisher

import com.newy.algotrade.common.coroutine.EventBus
import com.newy.algotrade.common.domain.extension.ProductPrice
import com.newy.algotrade.common.event.ReceivePollingPriceEvent
import com.newy.algotrade.product_price.domain.ProductPriceKey
import com.newy.algotrade.product_price.port.out.OnReceivePollingPricePort

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