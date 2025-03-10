package com.newy.algotrade.unit.notification_send.service

import com.newy.algotrade.notification_send.domain.NotificationApp
import com.newy.algotrade.notification_send.domain.NotificationSendMessage
import com.newy.algotrade.notification_send.domain.Webhook
import com.newy.algotrade.notification_send.port.`in`.GetMessageInPort
import com.newy.algotrade.notification_send.port.`in`.SaveMessageInPort
import com.newy.algotrade.notification_send.port.`in`.SendMessageInPort
import com.newy.algotrade.notification_send.port.`in`.model.SendNotificationMessageCommand
import com.newy.algotrade.notification_send.service.SendNotificationMessageFacadeService
import helpers.spring.MethodAnnotationTestHelper
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@DisplayName("SendNotificationMessageFacadeService 메서드 호출 순서 확인 테스트")
class SendNotificationMessageFacadeServiceCallStackTest : BaseSendNotificationMessageFacadeServiceTest() {
    private var log: String = ""

    @Test
    fun `service 는 아래와 같은 순서로 InPort 를 호출한다`() = runTest {
        val service = newSendNotificationMessageFacadeService()

        service.sendNotificationMessage(command = newSendNotificationMessageCommand())

        assertEquals("getMessage sendMessage saveMessage ", log)
    }

    override suspend fun getMessage(command: SendNotificationMessageCommand): NotificationSendMessage =
        super.getMessage(command).also {
            log += "getMessage "
        }

    override suspend fun sendMessage(notificationSendMessage: NotificationSendMessage): NotificationSendMessage =
        super.sendMessage(notificationSendMessage).also {
            log += "sendMessage "
        }

    override suspend fun saveMessage(notificationSendMessage: NotificationSendMessage): NotificationSendMessage =
        super.saveMessage(notificationSendMessage).also {
            log += "saveMessage "
        }
}

@DisplayName("SendNotificationMessageFacadeService 파라미터 전달 순서 확인 테스트")
class SendNotificationMessageFacadeServiceParameterTest : BaseSendNotificationMessageFacadeServiceTest() {
    @Test
    fun `getMessageInPort 의 return value 가 sendMessageInPort 의 parameter value 로 전달된다`() = runTest {
        var getMessageInPortReturnValue: NotificationSendMessage? = null
        var sendMessageInPortParameterValue: NotificationSendMessage? = null

        val service = newSendNotificationMessageFacadeService(
            getMessageInPort = {
                newNotificationSendMessage("GET").also { returnValue ->
                    getMessageInPortReturnValue = returnValue
                }
            },
            sendMessageInPort = { parameterValue ->
                newNotificationSendMessage("SEND").also {
                    sendMessageInPortParameterValue = parameterValue
                }
            },
        )

        service.sendNotificationMessage(command = newSendNotificationMessageCommand())

        assertNotNull(getMessageInPortReturnValue)
        assertEquals(getMessageInPortReturnValue, sendMessageInPortParameterValue)
    }

    @Test
    fun `sendMessageInPort 의 return value 가 saveMessageInPort 의 parameter value 로 전달된다`() = runTest {
        var sendMessageInPortReturnValue: NotificationSendMessage? = null
        var saveMessageInPortParameterValue: NotificationSendMessage? = null

        val service = newSendNotificationMessageFacadeService(
            sendMessageInPort = {
                newNotificationSendMessage("SEND").also { returnValue ->
                    sendMessageInPortReturnValue = returnValue
                }
            },
            saveMessageInPort = { parameterValue ->
                newNotificationSendMessage("SAVE").also {
                    saveMessageInPortParameterValue = parameterValue
                }
            }
        )

        service.sendNotificationMessage(command = newSendNotificationMessageCommand())

        assertNotNull(sendMessageInPortReturnValue)
        assertEquals(sendMessageInPortReturnValue, saveMessageInPortParameterValue)
    }
}

class SendNotificationMessageFacadeServiceAnnotationTest {
    @Test
    fun `메서드 애너테이션 사용 여부 확인`() {
        assertTrue(MethodAnnotationTestHelper(SendNotificationMessageFacadeService::sendNotificationMessage).hasNotTransactionalAnnotation())
    }
}

open class BaseSendNotificationMessageFacadeServiceTest : GetMessageInPort, SendMessageInPort, SaveMessageInPort {
    protected fun newSendNotificationMessageFacadeService(
        getMessageInPort: GetMessageInPort = this,
        sendMessageInPort: SendMessageInPort = this,
        saveMessageInPort: SaveMessageInPort = this,
    ) = SendNotificationMessageFacadeService(
        getMessageInPort = getMessageInPort,
        sendMessageInPort = sendMessageInPort,
        saveMessageInPort = saveMessageInPort,
    )

    protected fun newSendNotificationMessageCommand() =
        SendNotificationMessageCommand(
            userId = 1,
            message = "test message",
            isVerified = true,
        )

    protected fun newNotificationSendMessage(requestMessage: String = "") =
        NotificationSendMessage(
            notificationApp = NotificationApp(
                id = 1,
                webhook = Webhook.from(
                    type = "SLACK",
                    url = "http://localhost:8080"
                )
            ),
            requestMessage = requestMessage
        )

    override suspend fun getMessage(command: SendNotificationMessageCommand): NotificationSendMessage =
        newNotificationSendMessage()

    override suspend fun sendMessage(notificationSendMessage: NotificationSendMessage): NotificationSendMessage =
        newNotificationSendMessage()

    override suspend fun saveMessage(notificationSendMessage: NotificationSendMessage): NotificationSendMessage =
        newNotificationSendMessage()
}