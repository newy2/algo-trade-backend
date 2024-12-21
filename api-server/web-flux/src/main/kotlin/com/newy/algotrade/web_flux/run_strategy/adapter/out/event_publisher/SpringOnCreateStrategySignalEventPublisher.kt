package com.newy.algotrade.web_flux.run_strategy.adapter.out.event_publisher

import com.newy.algotrade.common.coroutine.EventBus
import com.newy.algotrade.common.event.CreateStrategySignalEvent
import com.newy.algotrade.run_strategy.adapter.out.event_publisher.OnCreateStrategySignalEventPublisher
import com.newy.algotrade.web_flux.common.annotation.EventPublisherAdapter

@EventPublisherAdapter
class SpringOnCreateStrategySignalEventPublisher(
    eventBus: EventBus<CreateStrategySignalEvent>
) : OnCreateStrategySignalEventPublisher(eventBus)