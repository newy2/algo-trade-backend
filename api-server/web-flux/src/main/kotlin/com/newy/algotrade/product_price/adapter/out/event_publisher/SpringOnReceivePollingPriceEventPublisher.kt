package com.newy.algotrade.product_price.adapter.out.event_publisher

import com.newy.algotrade.common.annotation.EventPublisherAdapter
import com.newy.algotrade.common.coroutine.EventBus
import com.newy.algotrade.common.event.ReceivePollingPriceEvent

@EventPublisherAdapter
class SpringOnReceivePollingPriceEventPublisher(
    eventBus: EventBus<ReceivePollingPriceEvent>
) : OnReceivePollingPriceEventPublisher(eventBus)