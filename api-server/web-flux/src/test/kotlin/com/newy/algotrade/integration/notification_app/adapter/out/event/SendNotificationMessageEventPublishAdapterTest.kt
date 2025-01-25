package com.newy.algotrade.integration.notification_app.adapter.out.event

import com.newy.algotrade.common.event.SendNotificationMessageEvent
import com.newy.algotrade.notification_app.adapter.out.event.SendNotificationMessageEventPublishAdapter
import helpers.spring.BaseSpringBootTest
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import kotlin.test.assertEquals

var publishedEvent: SendNotificationMessageEvent? = null

@Component("sendNotificationMessageEventListenerForTest")
class SendNotificationMessageEventListener {
    @EventListener
    fun onApplicationEvent(event: SendNotificationMessageEvent) {
        publishedEvent = event
    }
}

@DisplayName("알림 메세지 전송 요청 이벤트 어댑터 테스트")
class SendNotificationMessageEventPublishAdapterTest(
    @Autowired private val adapter: SendNotificationMessageEventPublishAdapter,
) : BaseSpringBootTest() {
    @Test
    fun `SendNotificationMessageEventPublishAdapter#send 메서드 호출 시 SendNotificationMessageEvent 이벤트가 발행 된다`() = runTest {
        val event = SendNotificationMessageEvent(
            userId = 1,
            message = "test message",
            isVerified = false,
        )
        adapter.send(event)

        assertEquals(publishedEvent, event)
    }
}