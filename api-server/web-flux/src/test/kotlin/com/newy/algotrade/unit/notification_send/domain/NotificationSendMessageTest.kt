package com.newy.algotrade.unit.notification_send.domain

import com.newy.algotrade.notification_send.domain.NotificationApp
import com.newy.algotrade.notification_send.domain.NotificationSendMessage
import com.newy.algotrade.notification_send.domain.Webhook
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

val fakeNotificationApp = NotificationApp(
    id = 2,
    webhook = Webhook.from(
        type = "SLACK",
        url = "https://hooks.slack.com/services/1111",
    )
)

@DisplayName("알림 메세지 생성 테스트")
class NotificationSendMessageTest {
    @Test
    fun `알림 메세지 생성하기`() {
        val message = NotificationSendMessage(
            notificationApp = fakeNotificationApp,
            requestMessage = "test message"
        )

        assertEquals(
            NotificationSendMessage(
                id = 0,
                notificationApp = fakeNotificationApp,
                requestMessage = "test message",
                responseMessage = "",
                status = NotificationSendMessage.Status.SENDING
            ),
            message
        )
    }
}

@DisplayName("메세지 전송 응답 메세지 설정 테스트")
class NotificationSendMessageSetResponseMessageTest {
    private val message = NotificationSendMessage(
        id = 1,
        notificationApp = fakeNotificationApp,
        requestMessage = "test message"
    )

    @Test
    fun `메세지 전송에 성공한 경우`() {
        assertEquals(
            NotificationSendMessage(
                id = 1,
                notificationApp = fakeNotificationApp,
                requestMessage = "test message",
                responseMessage = "ok",
                status = NotificationSendMessage.Status.SUCCEED
            ),
            message.succeed("ok")
        )
    }

    @Test
    fun `메세지 전송에 실패한 경우`() {
        assertEquals(
            NotificationSendMessage(
                id = 1,
                notificationApp = fakeNotificationApp,
                requestMessage = "test message",
                responseMessage = "fail",
                status = NotificationSendMessage.Status.FAILED
            ),
            message.failed("fail")
        )
    }
}

@DisplayName("슬렉 메세지 생성 테스트")
class SlackNotificationSendMessageTest {
    @Test
    fun `슬렉 전송 메세지 형식`() {
        val message = NotificationSendMessage(
            notificationApp = fakeNotificationApp,
            requestMessage = "test message"
        )

        assertEquals(
            mapOf(
                "blocks" to listOf(
                    mapOf(
                        "type" to "section",
                        "text" to mapOf(
                            "type" to "mrkdwn",
                            "text" to "test message"
                        )
                    )
                )
            ),
            message.getRequestBody()
        )
    }

    @Test
    fun `슬렉 URL Path 조회`() {
        val message = NotificationSendMessage(
            notificationApp = fakeNotificationApp,
            requestMessage = "test message"
        )

        assertEquals("/services/1111", message.getUrlPath())
    }
}