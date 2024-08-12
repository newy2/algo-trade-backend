package com.newy.algotrade.web_flux.product_price.adapter.out.event_publisher

import com.newy.algotrade.coroutine_based_application.common.coroutine.EventBus
import com.newy.algotrade.coroutine_based_application.common.event.ReceivePollingPriceEvent
import com.newy.algotrade.coroutine_based_application.product_price.adapter.out.event_publisher.OnReceivePollingPriceEventPublisher
import com.newy.algotrade.web_flux.common.annotation.EventPublisherAdapter

@EventPublisherAdapter
class SpringOnReceivePollingPriceEventPublisher(
    eventBus: EventBus<ReceivePollingPriceEvent>
) : OnReceivePollingPriceEventPublisher(eventBus)