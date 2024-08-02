package com.newy.algotrade.unit.notification.service

import com.newy.algotrade.coroutine_based_application.common.coroutine.EventBus
import com.newy.algotrade.coroutine_based_application.common.event.SendNotificationEvent
import com.newy.algotrade.coroutine_based_application.common.web.http.HttpApiClient
import com.newy.algotrade.coroutine_based_application.notification.port.`in`.model.SendNotificationCommand
import com.newy.algotrade.coroutine_based_application.notification.port.out.SendNotificationLogPort
import com.newy.algotrade.coroutine_based_application.notification.service.SendNotificationCommandService
import com.newy.algotrade.domain.common.consts.NotificationAppType
import com.newy.algotrade.domain.common.consts.NotificationRequestMessageFormat
import com.newy.algotrade.domain.common.consts.SendNotificationLogStatus
import com.newy.algotrade.domain.common.consts.SlackNotificationRequestMessageFormat
import com.newy.algotrade.domain.common.exception.PreconditionError
import com.newy.algotrade.domain.notification.SendNotificationLog
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.fail

@DisplayName("메소드 호출 순서 확인")
class SendNotificationCommandServiceTest : NoErrorSendNotificationAdapter() {
    private val methodCallLogs: MutableList<String> = mutableListOf()
    private lateinit var eventBus: EventBus<SendNotificationEvent>
    private lateinit var service: SendNotificationCommandService

    override suspend fun createByStatusRequested(notificationAppId: Long, requestMessage: String): Long {
        methodCallLogs.add("createByStatusRequested")
        return super.createByStatusRequested(notificationAppId, requestMessage)
    }

    override suspend fun getSendNotificationLog(notificationLogId: Long): SendNotificationLog {
        methodCallLogs.add("getSendNotificationLog")
        return super.getSendNotificationLog(notificationLogId)
    }

    override suspend fun saveSendNotificationLog(domainEntity: SendNotificationLog): Boolean {
        methodCallLogs.add("saveSendNotificationLog(${domainEntity.status})")
        return super.saveSendNotificationLog(domainEntity)
    }

    @BeforeEach
    fun setUp() {
        methodCallLogs.clear()
        eventBus = EventBus()
        service = SendNotificationCommandService(
            adapter = this,
            eventBus = eventBus,
            httpApiClient = object : NoErrorHttpClient() {
                override suspend fun <T : Any> _post(
                    path: String,
                    params: Map<String, String>,
                    body: Any,
                    headers: Map<String, String>,
                    jsonExtraValues: Map<String, Any>,
                    clazz: KClass<T>
                ): T {
                    methodCallLogs.add("HttpClient._post")
                    return super._post(path, params, body, headers, jsonExtraValues, clazz)
                }
            }
        )
    }

    @Test
    fun `메세지 전송 요청하기`() = runTest {
        eventBus.addListener(coroutineContext) {
            methodCallLogs.add("onReceiveEvent")
        }
        delay(1000)

        service.requestSendNotification(
            SendNotificationCommand(
                notificationAppId = 1,
                requestMessage = "message"
            )
        )
        coroutineContext.cancelChildren()

        assertEquals(listOf("createByStatusRequested", "onReceiveEvent"), methodCallLogs)
    }

    @Test
    fun `메세지 전송 하기`() = runTest {
        service.sendNotification(SendNotificationEvent(sendNotificationLogId = 1))

        assertEquals(
            listOf(
                "getSendNotificationLog",
                "saveSendNotificationLog(PROCESSING)",
                "HttpClient._post",
                "saveSendNotificationLog(SUCCEED)"
            ),
            methodCallLogs
        )
    }
}

@DisplayName("예외사항 테스트")
class ErrorSendNotificationCommandServiceTest {
    @Test
    fun `REQUESTED 가 아닌 SendNotificationLog 로 알림 전송을 시도하는 경우`() = runTest {
        SendNotificationLogStatus
            .values()
            .filter { it != SendNotificationLogStatus.REQUESTED }
            .forEach { notSupportedPreStatus ->
                val service = SendNotificationCommandService(
                    adapter = object : NoErrorSendNotificationAdapter() {
                        override suspend fun getSendNotificationLog(notificationLogId: Long) =
                            super.getSendNotificationLog(notificationLogId).copy(
                                status = notSupportedPreStatus
                            )
                    },
                    eventBus = EventBus(),
                    httpApiClient = NoErrorHttpClient()
                )

                try {
                    service.sendNotification(SendNotificationEvent(sendNotificationLogId = 1))
                    fail()
                } catch (e: PreconditionError) {
                    assertEquals("REQUESTED 상태만 변경 가능합니다. (status: ${notSupportedPreStatus.name})", e.message)
                }
            }
    }
}

@DisplayName("HTTP 호출 파라미터 확인")
class SendNotificationCommandServiceHttpClientTest() {
    @Test
    fun `http client 호출 파라미터 확인`() = runTest {
        var calledPath = ""
        var calledBody: NotificationRequestMessageFormat? = null

        val slackSendNotificationAdapter = object : NoErrorSendNotificationAdapter() {
            override suspend fun getSendNotificationLog(notificationLogId: Long): SendNotificationLog {
                return super.getSendNotificationLog(notificationLogId).copy(
                    notificationAppType = NotificationAppType.SLACK,
                    url = "${NotificationAppType.SLACK.host}/services/TXXXX/BXXXX/abc123",
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

        val service = SendNotificationCommandService(
            adapter = slackSendNotificationAdapter,
            eventBus = EventBus(),
            httpApiClient = spyHttpApiClient,
        )
        service.sendNotification(SendNotificationEvent(sendNotificationLogId = 1))

        assertEquals("/services/TXXXX/BXXXX/abc123", calledPath)
        assertEquals(SlackNotificationRequestMessageFormat.from("request message"), calledBody)
    }
}

open class NoErrorSendNotificationAdapter : SendNotificationLogPort {
    override suspend fun createByStatusRequested(notificationAppId: Long, requestMessage: String): Long = 1
    override suspend fun getSendNotificationLog(notificationLogId: Long): SendNotificationLog = SendNotificationLog(
        sendNotificationLogId = 1,
        notificationAppId = 2,
        notificationAppType = NotificationAppType.SLACK,
        status = SendNotificationLogStatus.REQUESTED,
        url = "",
        requestMessage = "",
    )

    override suspend fun saveSendNotificationLog(domainEntity: SendNotificationLog): Boolean = true
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