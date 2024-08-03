package com.newy.algotrade.web_flux.product.adapter.out.event_publisher

import com.newy.algotrade.coroutine_based_application.common.coroutine.EventBus
import com.newy.algotrade.coroutine_based_application.common.event.ReceivePollingPriceEvent
import com.newy.algotrade.coroutine_based_application.product.adapter.out.event_publisher.OnReceivePollingPriceEventPublisher
import org.springframework.stereotype.Component

@Component
class SpringOnReceivePollingPriceEventPublisher(
    eventBus: EventBus<ReceivePollingPriceEvent>
) : OnReceivePollingPriceEventPublisher(eventBus)