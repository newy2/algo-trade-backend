package com.newy.algotrade.unit.notification_send.service

import com.newy.algotrade.common.exception.HttpResponseException
import com.newy.algotrade.common.exception.NotFoundRowException
import com.newy.algotrade.notification_send.domain.NotificationApp
import com.newy.algotrade.notification_send.domain.NotificationSendMessage
import com.newy.algotrade.notification_send.domain.Webhook
import com.newy.algotrade.notification_send.port.`in`.model.SendNotificationMessageCommand
import com.newy.algotrade.notification_send.port.out.FindNotificationAppOutPort
import com.newy.algotrade.notification_send.port.out.SaveNotificationSendMessageOutPort
import com.newy.algotrade.notification_send.port.out.SendNotificationMessageOutPort
import com.newy.algotrade.notification_send.service.SendNotificationMessageCommandService
import com.newy.algotrade.notification_send.service.SendNotificationMessageFacadeService
import helpers.spring.MethodAnnotationTestHelper
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SendNotificationMessageFacadeServiceTest {
    private val command = SendNotificationMessageCommand(
        userId = 1,
        message = "test message",
        isVerified = true,
    )
    private val fakeNotificationApp = NotificationApp(
        id = 1,
        webhook = Webhook.from(
            type = "SLACK",
            url = "https://hooks.slack.com/services/1111",
        )
    )
    private val fakeSavedNotificationSendMessageId = 10.toLong()

    @Test
    fun `NotificationApp 을 찾지 못하면 NotFoundRowException 에러가 발생한다`() = runTest {
        val notFoundAdapter = FindNotificationAppOutPort { _, _ -> null }
        val service = createFacadeService(
            commandService = createService(
                findNotificationAppOutPort = notFoundAdapter,
            ),
        )

        val error = assertThrows<NotFoundRowException> {
            service.sendNotificationMessage(command)
        }

        assertEquals("사용자 알림앱 정보를 찾을 수 없습니다. (userId: 1, isVerified: true)", error.message)
    }

    @Test
    fun `sendMessage 메서드에서 에러가 발생하면 failed 메세지가 리턴된다`() = runTest {
        val errorSendMessageAdapter = SendNotificationMessageOutPort {
            throw HttpResponseException(responseMessage = "invalid_payload")
        }
        val service = createFacadeService(
            commandService = createService(
                sendNotificationMessageOutPort = errorSendMessageAdapter,
            )
        )

        val result = service.sendNotificationMessage(command)

        assertEquals(
            NotificationSendMessage(
                id = fakeSavedNotificationSendMessageId,
                notificationApp = fakeNotificationApp,
                requestMessage = "test message",
                responseMessage = "invalid_payload",
                status = NotificationSendMessage.Status.FAILED
            ),
            result
        )
    }

    @Test
    fun `에러가 없으면 SUCCEED 상태의 메세지를 리턴한다`() = runTest {
        val service = createFacadeService()
        val result = service.sendNotificationMessage(command)

        assertEquals(
            NotificationSendMessage(
                id = fakeSavedNotificationSendMessageId,
                notificationApp = fakeNotificationApp,
                requestMessage = "test message",
                responseMessage = "ok",
                status = NotificationSendMessage.Status.SUCCEED
            ),
            result
        )
    }

    private fun createFacadeService(
        commandService: SendNotificationMessageCommandService = createService()
    ) = SendNotificationMessageFacadeService(commandService)

    private fun createService(
        findNotificationAppOutPort: FindNotificationAppOutPort = FindNotificationAppOutPort { _, _ -> fakeNotificationApp },
        sendNotificationMessageOutPort: SendNotificationMessageOutPort = SendNotificationMessageOutPort { "ok" },
        saveNotificationSendMessageOutPort: SaveNotificationSendMessageOutPort = SaveNotificationSendMessageOutPort { fakeSavedNotificationSendMessageId },
    ) = SendNotificationMessageCommandService(
        findNotificationAppOutPort,
        sendNotificationMessageOutPort,
        saveNotificationSendMessageOutPort,
    )
}

class SendNotificationMessageFacadeServiceAnnotationTest {
    @Test
    fun `메서드 애너테이션 사용 여부 확인`() {
        assertTrue(MethodAnnotationTestHelper(SendNotificationMessageFacadeService::sendNotificationMessage).hasNotTransactionalAnnotation())

        assertTrue(MethodAnnotationTestHelper(SendNotificationMessageCommandService::getMessage).hasReadOnlyTransactionalAnnotation())
        assertTrue(MethodAnnotationTestHelper(SendNotificationMessageCommandService::sendMessage).hasNotTransactionalAnnotation())
        assertTrue(MethodAnnotationTestHelper(SendNotificationMessageCommandService::saveMessage).hasWritableTransactionalAnnotation())
    }
}