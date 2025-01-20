package com.newy.algotrade.study.spring.event

import helpers.spring.BaseSpringBootTest
import kotlinx.coroutines.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import kotlin.test.assertEquals

var backgroundScopeError: Throwable? = null

data class TestThrowErrorInOtherCoroutineEvent(
    val message: String,
)

@Component
class TestThrowErrorInOtherCoroutineEventListener {
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        backgroundScopeError = throwable
    }

    @EventListener
    fun onApplicationEvent(event: TestThrowErrorInOtherCoroutineEvent) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            throw RuntimeException("BACKGROUND SCOPE ERROR")
        }
    }
}

@DisplayName("코루틴 기반 비동기 이벤트 리스너 에러 전파 테스트")
class AsyncApplicationEventThrowErrorTest(
    @Autowired private val publisher: ApplicationEventPublisher,
) : BaseSpringBootTest() {
    @Test
    fun `비동기 이벤트 리스너에서 에러가 발생한 경우 Event Publisher 에게 전파되지 않는다`(): Unit = runBlocking {
        assertDoesNotThrow {
            publisher.publishEvent(TestThrowErrorInOtherCoroutineEvent("message"))
            delay(200) // wait for async listener
        }
        assertEquals("BACKGROUND SCOPE ERROR", backgroundScopeError?.message)
    }
}