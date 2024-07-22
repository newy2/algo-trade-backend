package com.newy.algotrade.unit.common.coroutine

import com.newy.algotrade.coroutine_based_application.common.coroutine.EventBus
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class EventBusTest {
    @Test
    fun `브로드캐스트 이벤트 발행 구독 테스트`() = runTest {
        val eventBus = EventBus<String>()
        val list1 = mutableListOf<String>()
        val list2 = mutableListOf<String>()

        eventBus.publishEvent("0")

        eventBus.addListener(coroutineContext) {
            list1.add(it)
        }

        delay(1000)
        eventBus.publishEvent("1")
        eventBus.publishEvent("2")

        eventBus.addListener(coroutineContext) {
            list2.add(it)
        }

        delay(1000)
        eventBus.publishEvent("3")
        eventBus.publishEvent("4")

        coroutineContext.cancelChildren()

        Assertions.assertEquals(listOf("1", "2", "3", "4"), list1)
        Assertions.assertEquals(listOf("3", "4"), list2)
    }
}
