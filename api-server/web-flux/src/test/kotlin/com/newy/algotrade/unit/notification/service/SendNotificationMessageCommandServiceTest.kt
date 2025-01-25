package com.newy.algotrade.unit.notification.service

import com.newy.algotrade.common.exception.HttpResponseException
import com.newy.algotrade.common.exception.NotFoundRowException
import com.newy.algotrade.notification.domain.NotificationApp
import com.newy.algotrade.notification.domain.NotificationSendMessage
import com.newy.algotrade.notification.domain.Webhook
import com.newy.algotrade.notification.port.`in`.model.SendNotificationMessageCommand
import com.newy.algotrade.notification.port.out.FindNotificationAppOutPort
import com.newy.algotrade.notification.port.out.SaveNotificationSendMessageOutPort
import com.newy.algotrade.notification.port.out.SendNotificationMessageOutPort
import com.newy.algotrade.notification.service.SendNotificationMessageCommandService
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

@DisplayName("SendNotificationMessageCommandService 테스트")
class HappyPathSendNotificationMessageCommandServiceTest {
    private val service = newService()
    private val command = SendNotificationMessageCommand(
        userId = 1,
        message = "test message",
        isVerified = true,
    )

    @Test
    fun `NotificationSendMessage 생성하기`() = runTest {
        val message = service.getMessage(command)

        assertEquals(
            NotificationSendMessage(
                id = 0,
                notificationApp = FAKE_NOTIFICATION_APP,
                requestMessage = "test message",
                responseMessage = "",
                status = NotificationSendMessage.Status.SENDING
            ),
            message
        )
    }

    @Test
    fun `NotificationSendMessage 전송하기`() = runTest {
        val message = service.getMessage(command)
        val successMessage = service.sendMessage(message)

        assertEquals(
            NotificationSendMessage(
                id = 0,
                notificationApp = FAKE_NOTIFICATION_APP,
                requestMessage = "test message",
                responseMessage = "ok",
                status = NotificationSendMessage.Status.SUCCEED
            ),
            successMessage
        )
    }

    @Test
    fun `NotificationSendMessage 저장하기`() = runTest {
        val message = service.getMessage(command)
        val successMessage = service.sendMessage(message)
        val savedMessage = service.saveMessage(successMessage)

        assertEquals(
            NotificationSendMessage(
                id = FAKE_SAVED_NOTIFICATION_SEND_MESSAGE_ID,
                notificationApp = FAKE_NOTIFICATION_APP,
                requestMessage = "test message",
                responseMessage = "ok",
                status = NotificationSendMessage.Status.SUCCEED
            ),
            savedMessage
        )
    }
}

@DisplayName("SendNotificationMessageCommandService 예외처리 테스트")
class SendNotificationMessageCommandServiceExceptionTest {
    private val command = SendNotificationMessageCommand(
        userId = 1,
        message = "test message",
        isVerified = true,
    )

    @Test
    fun `getMessage 메서드에서 NotificationAp 을 찾지 못하면 NotFoundRowException 에러가 발생한다`() = runTest {
        val notFoundNotificationAppAdapter = FindNotificationAppOutPort { _, _ -> null }
        val service = newService(
            findNotificationAppOutPort = notFoundNotificationAppAdapter,
        )

        val error = assertThrows<NotFoundRowException> {
            service.getMessage(command)
        }

        assertEquals("사용자 알림앱 정보를 찾을 수 없습니다. (userId: 1, isVerified: true)", error.message)
    }

    @Test
    fun `sendMessage 메서드에서 에러가 발생하면 failed 메세지가 리턴된다`() = runTest {
        val errorSendMessageAdapter = SendNotificationMessageOutPort {
            throw HttpResponseException(responseMessage = "invalid_payload")
        }
        val service = newService(
            sendNotificationMessageOutPort = errorSendMessageAdapter,
        )

        val message = service.getMessage(command)
        val failMessage = service.sendMessage(message)

        assertEquals(
            NotificationSendMessage(
                id = 0,
                notificationApp = FAKE_NOTIFICATION_APP,
                requestMessage = "test message",
                responseMessage = "invalid_payload",
                status = NotificationSendMessage.Status.FAILED
            ),
            failMessage
        )
    }
}


val FAKE_NOTIFICATION_APP = NotificationApp(
    id = 1,
    webhook = Webhook.from(
        type = "SLACK",
        url = "https://hooks.slack.com/services/1111",
    )
)

const val FAKE_SAVED_NOTIFICATION_SEND_MESSAGE_ID = 10.toLong()

class DefaultNotificationAppAdapter : FindNotificationAppOutPort {
    override suspend fun findNotificationApp(userId: Long, isVerified: Boolean) = FAKE_NOTIFICATION_APP
}

class DefaultSendNotificationMessageAdapter : SendNotificationMessageOutPort {
    override suspend fun sendMessage(notificationSendMessage: NotificationSendMessage) = "ok"
}

class DefaultNotificationSendMessageAdapter : SaveNotificationSendMessageOutPort {
    override suspend fun saveNotificationSendMessage(notificationSendMessage: NotificationSendMessage) =
        FAKE_SAVED_NOTIFICATION_SEND_MESSAGE_ID
}

fun newService(
    findNotificationAppOutPort: FindNotificationAppOutPort = DefaultNotificationAppAdapter(),
    sendNotificationMessageOutPort: SendNotificationMessageOutPort = DefaultSendNotificationMessageAdapter(),
    saveNotificationSendMessageOutPort: SaveNotificationSendMessageOutPort = DefaultNotificationSendMessageAdapter(),
) = SendNotificationMessageCommandService(
    findNotificationAppOutPort,
    sendNotificationMessageOutPort,
    saveNotificationSendMessageOutPort,
)