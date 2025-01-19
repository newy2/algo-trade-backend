package com.newy.algotrade.study.spring.event

import helpers.spring.BaseSpringBootTest
import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.stereotype.Component
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

data class AsyncThreadCountTestEvent(val message: String)

@Component
@EnableAsync
class AsyncEventListener {
    @Async
    @EventListener
    fun handleEvent(event: AsyncThreadCountTestEvent) {
        Thread.sleep(10)
    }
}

data class ThreadCountTestEvent(val message: String)

@Component
class EventListener {
    @EventListener
    fun onApplicationEvent(event: ThreadCountTestEvent) {
        Thread.sleep(10)
    }
}

data class CoroutineThreadCountTestEvent(val message: String)

@Component
class CoroutineEventListener {
    @EventListener
    fun onApplicationEvent(event: CoroutineThreadCountTestEvent) {
        CoroutineScope(Dispatchers.IO).launch {
            delay(10)
        }
    }
}

class AsyncEventListenerTest(
    @Autowired private val eventPublisher: ApplicationEventPublisher,
) : BaseSpringBootTest() {
    @Test
    fun `@Async 기반 비동기 이벤트 리스너는 추가 쓰레드를 사용한다`() = runBlocking {
        val initialThreadCount = Thread.activeCount()
        repeat(5) {
            eventPublisher.publishEvent(AsyncThreadCountTestEvent("Event $it"))
            delay(20)
        }
        val finalThreadCount = Thread.activeCount()

        assertNotEquals(finalThreadCount, initialThreadCount)
        assertTrue(finalThreadCount > initialThreadCount)
    }
}

class SyncEventListenerTest(
    @Autowired private val eventPublisher: ApplicationEventPublisher,
) : BaseSpringBootTest() {
    @Test
    fun `동기 이벤트 리스너는 쓰레드 변화량이 없다`() = runBlocking {
        val initialThreadCount = Thread.activeCount()
        repeat(5) {
            eventPublisher.publishEvent(ThreadCountTestEvent("Event $it"))
            delay(20)
        }
        val finalThreadCount = Thread.activeCount()

        assertEquals(finalThreadCount, initialThreadCount)
    }
}

class CoroutineEventListenerTest(
    @Autowired private val eventPublisher: ApplicationEventPublisher,
) : BaseSpringBootTest() {
    @Test
    fun `코루틴 기반 비동기 이벤트 리스너는 쓰레드 변화량이 없다`() = runBlocking {
        val initialThreadCount = Thread.activeCount()
        repeat(5) {
            eventPublisher.publishEvent(CoroutineThreadCountTestEvent("Event $it"))
            delay(20)
        }
        val finalThreadCount = Thread.activeCount()

        assertEquals(finalThreadCount, initialThreadCount)
    }
}