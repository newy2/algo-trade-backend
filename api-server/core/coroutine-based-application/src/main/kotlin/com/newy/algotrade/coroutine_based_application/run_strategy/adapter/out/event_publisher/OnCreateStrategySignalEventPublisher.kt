package com.newy.algotrade.coroutine_based_application.run_strategy.adapter.out.event_publisher

import com.newy.algotrade.coroutine_based_application.common.coroutine.EventBus
import com.newy.algotrade.coroutine_based_application.common.event.CreateStrategySignalEvent
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.OnCreatedStrategySignalPort
import com.newy.algotrade.domain.chart.strategy.StrategySignal

open class OnCreateStrategySignalEventPublisher(
    private val eventBus: EventBus<CreateStrategySignalEvent>
) : OnCreatedStrategySignalPort {
    override suspend fun onCreatedSignal(userStrategyId: String, signal: StrategySignal) {
        eventBus.publishEvent(
            CreateStrategySignalEvent(
                userStrategyId = userStrategyId,
                strategySignal = signal,
            )
        )
    }
}