package com.newy.algotrade.study.spring.event

import helpers.spring.BaseSpringBootTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import kotlin.test.assertEquals

data class TestThrowErrorEvent(
    val message: String,
)

@Component
class TestEventListenerThrowError {
    @EventListener
    fun onApplicationEvent(event: TestThrowErrorEvent) {
        throw RuntimeException("ERROR")
    }
}

@DisplayName("동기 이벤트 리스너 에러 전파 테스트")
class SyncEventListenerThrowErrorTest(
    @Autowired private val publisher: ApplicationEventPublisher,
) : BaseSpringBootTest() {
    @Test
    fun `동기 이벤트 리스너에서 에러가 발생한 경우 Event Publisher 에게 전파된다`() {
        val error = assertThrows<RuntimeException> {
            publisher.publishEvent(TestThrowErrorEvent("message"))
        }

        assertEquals("ERROR", error.message)
    }
}