package com.newy.algotrade.unit.run_strategy.adapter.out.event_publisher

import com.newy.algotrade.chart.domain.Candle
import com.newy.algotrade.chart.domain.order.OrderType
import com.newy.algotrade.chart.domain.strategy.StrategySignal
import com.newy.algotrade.common.coroutine.EventBus
import com.newy.algotrade.common.event.CreateStrategySignalEvent
import com.newy.algotrade.run_strategy.adapter.out.event_publisher.OnCreateStrategySignalEventPublisher
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime

class OnCreateStrategySignalEventPublisherTest {
    @Test
    fun `onReceivePrice 메소드가 호출되면 ReceivePollingPriceEvent 가 발행된다`() = runTest {
        var receiveMessage: CreateStrategySignalEvent? = null
        val adapter = EventBus<CreateStrategySignalEvent>()
            .also { eventBus ->
                eventBus.addListener(coroutineContext) {
                    receiveMessage = it
                }
                delay(1000) // wait for addListener
            }.let { eventBus ->
                OnCreateStrategySignalEventPublisher(eventBus)
            }
        val strategySignal = StrategySignal(
            orderType = OrderType.BUY,
            timeFrame = Candle.TimeRange(
                Duration.ofMinutes(1),
                OffsetDateTime.parse("2024-05-09T00:02+09:00")
            ),
            price = 2000.toBigDecimal()
        )

        adapter.onCreatedSignal(userStrategyId = 1, signal = strategySignal)
        coroutineContext.cancelChildren()

        assertEquals(CreateStrategySignalEvent(userStrategyId = 1, strategySignal = strategySignal), receiveMessage)
    }
}