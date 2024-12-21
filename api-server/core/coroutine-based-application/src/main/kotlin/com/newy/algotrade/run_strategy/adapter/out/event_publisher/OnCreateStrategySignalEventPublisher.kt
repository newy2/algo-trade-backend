package com.newy.algotrade.run_strategy.adapter.out.event_publisher

import com.newy.algotrade.chart.domain.strategy.StrategySignal
import com.newy.algotrade.common.coroutine.EventBus
import com.newy.algotrade.common.event.CreateStrategySignalEvent
import com.newy.algotrade.run_strategy.port.out.OnCreatedStrategySignalPort

open class OnCreateStrategySignalEventPublisher(
    private val eventBus: EventBus<CreateStrategySignalEvent>
) : OnCreatedStrategySignalPort {
    override suspend fun onCreatedSignal(userStrategyId: Long, signal: StrategySignal) {
        eventBus.publishEvent(
            CreateStrategySignalEvent(
                userStrategyId = userStrategyId,
                strategySignal = signal,
            )
        )
    }
}