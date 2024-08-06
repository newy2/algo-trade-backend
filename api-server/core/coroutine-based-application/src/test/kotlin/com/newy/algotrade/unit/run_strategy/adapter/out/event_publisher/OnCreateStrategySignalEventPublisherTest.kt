package com.newy.algotrade.unit.run_strategy.adapter.out.event_publisher

import com.newy.algotrade.coroutine_based_application.common.coroutine.EventBus
import com.newy.algotrade.coroutine_based_application.common.event.CreateStrategySignalEvent
import com.newy.algotrade.coroutine_based_application.run_strategy.adapter.out.event_publisher.OnCreateStrategySignalEventPublisher
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.order.OrderType
import com.newy.algotrade.domain.chart.strategy.StrategySignal
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

        adapter.onCreatedSignal(userStrategyId = "id1", signal = strategySignal)
        coroutineContext.cancelChildren()

        assertEquals(CreateStrategySignalEvent(userStrategyId = "id1", strategySignal = strategySignal), receiveMessage)
    }
}