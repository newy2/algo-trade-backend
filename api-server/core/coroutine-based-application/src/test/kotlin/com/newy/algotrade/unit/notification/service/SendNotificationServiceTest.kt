package com.newy.algotrade.unit.notification.service

import com.newy.algotrade.coroutine_based_application.common.coroutine.EventBus
import com.newy.algotrade.coroutine_based_application.common.event.SendNotificationEvent
import com.newy.algotrade.coroutine_based_application.common.web.http.HttpApiClient
import com.newy.algotrade.coroutine_based_application.notification.domain.SendNotification
import com.newy.algotrade.coroutine_based_application.notification.port.`in`.model.SendNotificationCommand
import com.newy.algotrade.coroutine_based_application.notification.port.out.SendNotificationPort
import com.newy.algotrade.coroutine_based_application.notification.service.SendNotificationService
import com.newy.algotrade.domain.common.consts.NotificationApp
import com.newy.algotrade.domain.common.consts.NotificationRequestMessageFormat
import com.newy.algotrade.domain.common.consts.SlackNotificationRequestMessageFormat
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass
import kotlin.test.assertEquals

open class NoErrorSendNotificationAdapter : SendNotificationPort {
    override suspend fun setStatusRequested(notificationAppId: Long, requestMessage: String): Long = 1
    override suspend fun putStatusProcessing(sendNotificationLogId: Long) = true
    override suspend fun putResponseMessage(sendNotificationLogId: Long, responseMessage: String) = true
    override suspend fun getSendNotification(notificationLogId: Long) = SendNotification(
        notificationApp = NotificationApp.SLACK,
        url = "",
        requestMessage = "",
    )
}

private open class NoErrorHttpClient : HttpApiClient {
    override suspend fun <T : Any> _post(
        path: String,
        params: Map<String, String>,
        body: Any,
        headers: Map<String, String>,
        jsonExtraValues: Map<String, Any>,
        clazz: KClass<T>
    ): T {
        return "ok" as T
    }

    override suspend fun <T : Any> _get(
        path: String,
        params: Map<String, String>,
        headers: Map<String, String>,
        jsonExtraValues: Map<String, Any>,
        clazz: KClass<T>
    ): T {
        TODO("Not yet implemented")
    }
}

@DisplayName("메소드 호출 순서 확인")
class SendNotificationServiceTest : NoErrorSendNotificationAdapter() {
    private lateinit var eventBus: EventBus<SendNotificationEvent>
    private lateinit var service: SendNotificationService
    private var log: String = ""

    @BeforeEach
    fun setUp() {
        eventBus = EventBus()
        service = SendNotificationService(
            adapter = this,
            eventBus = eventBus,
            httpApiClient = NoErrorHttpClient()
        )
        log = ""
    }

    @Test
    fun `메세지 전송 요청하기`() = runTest {
        eventBus.addListener(coroutineContext) {
            log += "onReceiveEvent "
        }
        delay(1000)

        service.requestSendNotification(
            SendNotificationCommand(
                notificationAppId = 1,
                requestMessage = "message"
            )
        )

        coroutineContext.cancelChildren()
        assertEquals("setStatusRequested onReceiveEvent ", log)
    }

    @Test
    fun `메세지 전송 하기`() = runTest {
        service.sendNotification(SendNotificationEvent(sendNotificationLogId = 1))

        assertEquals("putStatusProcessing getSendNotification putResponseMessage ", log)
    }

    override suspend fun putStatusProcessing(sendNotificationLogId: Long): Boolean {
        log += "putStatusProcessing "
        return super.putStatusProcessing(sendNotificationLogId)
    }

    override suspend fun putResponseMessage(sendNotificationLogId: Long, responseMessage: String): Boolean {
        log += "putResponseMessage "
        return super.putResponseMessage(sendNotificationLogId, responseMessage)
    }

    override suspend fun setStatusRequested(notificationAppId: Long, requestMessage: String): Long {
        log += "setStatusRequested "
        return super.setStatusRequested(notificationAppId, requestMessage)
    }

    override suspend fun getSendNotification(notificationLogId: Long): SendNotification {
        log += "getSendNotification "
        return super.getSendNotification(notificationLogId)
    }
}

@DisplayName("HTTP 호출 파라미터 확인")
class SendNotificationServiceHttpClientTest() {
    @Test
    fun `http client 호출 파라미터 확인`() = runTest {
        var calledPath = ""
        var calledBody: NotificationRequestMessageFormat? = null

        val slackSendNotificationAdapter = object : NoErrorSendNotificationAdapter() {
            override suspend fun getSendNotification(notificationLogId: Long): SendNotification {
                return SendNotification(
                    notificationApp = NotificationApp.SLACK,
                    url = "${NotificationApp.SLACK.host}/services/TXXXX/BXXXX/abc123",
                    requestMessage = "request message"
                )
            }
        }
        val spyHttpApiClient = object : NoErrorHttpClient() {
            override suspend fun <T : Any> _post(
                path: String,
                params: Map<String, String>,
                body: Any,
                headers: Map<String, String>,
                jsonExtraValues: Map<String, Any>,
                clazz: KClass<T>
            ): T {
                calledPath = path
                calledBody = body as NotificationRequestMessageFormat
                return super._post(path, params, body, headers, jsonExtraValues, clazz)
            }
        }

        val service = SendNotificationService(
            adapter = slackSendNotificationAdapter,
            eventBus = EventBus(),
            httpApiClient = spyHttpApiClient,
        )
        service.sendNotification(SendNotificationEvent(sendNotificationLogId = 1))

        assertEquals("/services/TXXXX/BXXXX/abc123", calledPath)
        assertEquals(SlackNotificationRequestMessageFormat.from("request message"), calledBody)
    }
}