package com.newy.algotrade.study.spring.event

import helpers.spring.BaseSpringBootTest
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import kotlin.test.Test
import kotlin.test.assertEquals

val logs = mutableSetOf<String>()

data class TestEvent(
    val message: String,
)

@Component
class TestEventListener1 {
    @EventListener
    fun onApplicationEvent(event: TestEvent) {
        logs.add(event.message + "1")
    }
}

@Component
class TestEventListener2 {
    @EventListener
    fun onApplicationEvent(event: TestEvent) {
        logs.add(event.message + "2")
    }
}


@DisplayName("비동기 이벤트 리스너 테스트")
class ApplicationEventTest(
    @Autowired private val publisher: ApplicationEventPublisher,
) : BaseSpringBootTest() {
    @Test
    fun `ApplicationEvent 는 Topic 방식으로 Listener 에게 전달된다`() {
        publisher.publishEvent(TestEvent("message"))
        assertEquals(setOf("message1", "message2"), logs)
    }
}

