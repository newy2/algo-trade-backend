package com.newy.algotrade.run_strategy.adapter.out.event_publisher

import com.newy.algotrade.common.annotation.EventPublisherAdapter
import com.newy.algotrade.common.coroutine.EventBus
import com.newy.algotrade.common.event.CreateStrategySignalEvent

@EventPublisherAdapter
class SpringOnCreateStrategySignalEventPublisher(
    eventBus: EventBus<CreateStrategySignalEvent>
) : OnCreateStrategySignalEventPublisher(eventBus)