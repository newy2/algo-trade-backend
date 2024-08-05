package com.newy.algotrade.unit.product.adapter.out.event_publisher

import com.newy.algotrade.coroutine_based_application.common.coroutine.EventBus
import com.newy.algotrade.coroutine_based_application.common.event.ReceivePollingPriceEvent
import com.newy.algotrade.coroutine_based_application.product.adapter.out.event_publisher.OnReceivePollingPriceEventPublisher
import helpers.productPrice
import helpers.productPriceKey
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Duration

class OnReceivePollingPriceEventPublisherTest {
    @Test
    fun `onReceivePrice 메소드가 호출되면 ReceivePollingPriceEvent 가 발행된다`() = runTest {
        var receiveMessage: ReceivePollingPriceEvent? = null
        val adapter = EventBus<ReceivePollingPriceEvent>()
            .also { eventBus ->
                eventBus.addListener(coroutineContext) {
                    receiveMessage = it
                }
                delay(1000) // wait for addListener
            }.let { eventBus ->
                OnReceivePollingPriceEventPublisher(eventBus)
            }

        adapter.onReceivePrice(
            productPriceKey = productPriceKey("BTCUSDT"),
            productPriceList = listOf(productPrice(1000, Duration.ofMinutes(1)))
        )
        coroutineContext.cancelChildren()

        assertEquals(
            ReceivePollingPriceEvent(
                productPriceKey = productPriceKey("BTCUSDT"),
                productPriceList = listOf(productPrice(1000, Duration.ofMinutes(1)))
            ),
            receiveMessage
        )
    }
}