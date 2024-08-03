package com.newy.algotrade.unit.notification.service

import com.newy.algotrade.coroutine_based_application.common.coroutine.EventBus
import com.newy.algotrade.coroutine_based_application.common.event.SendNotificationEvent
import com.newy.algotrade.coroutine_based_application.common.web.http.HttpApiClient
import com.newy.algotrade.coroutine_based_application.notification.port.`in`.model.SendNotificationCommand
import com.newy.algotrade.coroutine_based_application.notification.port.out.CreateSendNotificationLogPort
import com.newy.algotrade.coroutine_based_application.notification.port.out.GetSendNotificationLogPort
import com.newy.algotrade.coroutine_based_application.notification.port.out.SaveSendNotificationLogPort
import com.newy.algotrade.coroutine_based_application.notification.port.out.SendNotificationLogPort
import com.newy.algotrade.coroutine_based_application.notification.service.SendNotificationCommandService
import com.newy.algotrade.domain.common.consts.NotificationAppType
import com.newy.algotrade.domain.common.consts.NotificationRequestMessageFormat
import com.newy.algotrade.domain.common.consts.SendNotificationLogStatus
import com.newy.algotrade.domain.common.consts.SendNotificationLogStatus.*
import com.newy.algotrade.domain.common.consts.SlackNotificationRequestMessageFormat
import com.newy.algotrade.domain.common.exception.NotFoundRowException
import com.newy.algotrade.domain.common.exception.PreconditionError
import com.newy.algotrade.domain.notification.SendNotificationLog
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.fail

private val incomingPortModel = SendNotificationCommand(
    notificationAppId = 1,
    requestMessage = "request message"
)

private val slackSendNotificationLog = SendNotificationLog(
    sendNotificationLogId = 1,
    notificationAppId = 2,
    notificationAppType = NotificationAppType.SLACK,
    status = REQUESTED,
    url = "",
    requestMessage = "",
)

@DisplayName("requestSendNotification 메소드 테스트")
class RequestSendNotificationTest {
    @Test
    fun `requestSendNotification 호출 완료시, SendNotificationEvent 가 발행된다`() = runTest {
        var publishedEvent: SendNotificationEvent? = null
        val eventBus = EventBus<SendNotificationEvent>().also {
            it.addListener(coroutineContext) { event ->
                publishedEvent = event
            }
            delay(1000) // wait for addListener
        }
        val savedId: Long = 100
        val service = newSendNotificationCommandService(
            createSendNotificationLogPort = { _, _ -> savedId },
            eventBus = eventBus
        )

        service.requestSendNotification(incomingPortModel)

        coroutineContext.cancelChildren()
        assertEquals(SendNotificationEvent(sendNotificationLogId = savedId), publishedEvent)
    }
}

@DisplayName("sendNotification 메소드 테스트")
class SendNotificationTest {
    private val beginSendNotificationLog = slackSendNotificationLog
    private val savedHistory = mutableListOf<SendNotificationLog>()
    private val getSendNotificationLogPort = GetSendNotificationLogPort { _ -> beginSendNotificationLog }
    private val saveSendNotificationLogPort = SaveSendNotificationLogPort { log -> savedHistory.add(log) }

    @BeforeEach
    fun setUp() {
        savedHistory.clear()
    }

    @Test
    fun `sendNotification 성공 메세지 수신 테스트`() = runTest {
        val slackSuccessMessage = "ok"
        val service = newSendNotificationCommandService(
            getSendNotificationLogPort = getSendNotificationLogPort,
            saveSendNotificationLogPort = saveSendNotificationLogPort,
            httpApiClient = newHttpClient { _, _ -> slackSuccessMessage },
        )

        service.sendNotification(SendNotificationEvent(sendNotificationLogId = 1))

        assertEquals(
            listOf(
                slackSendNotificationLog.copy(status = PROCESSING),
                slackSendNotificationLog.copy(status = SUCCEED, responseMessage = slackSuccessMessage),
            ),
            savedHistory
        )
    }

    @Test
    fun `sendNotification 실패 메세지 수신 테스트`() = runTest {
        val slackNotSuccessMessage = "not ok"
        val service = newSendNotificationCommandService(
            getSendNotificationLogPort = getSendNotificationLogPort,
            saveSendNotificationLogPort = saveSendNotificationLogPort,
            httpApiClient = newHttpClient { _, _ -> slackNotSuccessMessage },
        )

        service.sendNotification(SendNotificationEvent(sendNotificationLogId = 1))

        assertEquals(
            listOf(
                slackSendNotificationLog.copy(status = PROCESSING),
                slackSendNotificationLog.copy(status = FAILED, responseMessage = slackNotSuccessMessage),
            ),
            savedHistory
        )
    }
}

@DisplayName("HTTP 호출 파라미터 확인")
class SendNotificationCommandServiceHttpClientTest() {
    @Test
    fun `http client 가 호출하는 path 와 body 확인하기`() = runTest {
        var calledPath = ""
        var calledBody: NotificationRequestMessageFormat? = null
        val service = newSendNotificationCommandService(
            getSendNotificationLogPort = { _ ->
                slackSendNotificationLog.copy(
                    url = "${NotificationAppType.SLACK.host}/services/TXXXX/BXXXX/abc123",
                    requestMessage = "매수 알림"
                )
            },
            httpApiClient = newHttpClient { path, body ->
                calledPath = path
                calledBody = body as NotificationRequestMessageFormat
                "ok"
            },
        )

        service.sendNotification(SendNotificationEvent(sendNotificationLogId = 1))

        assertEquals("/services/TXXXX/BXXXX/abc123", calledPath)
        assertEquals(SlackNotificationRequestMessageFormat.from("매수 알림"), calledBody)
    }
}

@DisplayName("예외사항 테스트")
class SendNotificationCommandServiceExceptionTest {
    @Test
    fun `저장되지 않은 ID 로 sendNotification 을 호출하는 경우`() = runTest {
        val service = newSendNotificationCommandService(
            getSendNotificationLogPort = { _ -> null },
        )
        try {
            service.sendNotification(SendNotificationEvent(sendNotificationLogId = 2))
            fail()
        } catch (e: NotFoundRowException) {
            assertEquals("sendNotificationLogId 를 찾을 수 없습니다. (id: 2)", e.message)
        }
    }

    @Test
    fun `REQUESTED 이외의 상태로 sendNotification 을 호출하는 경우`() = runTest {
        SendNotificationLogStatus
            .values()
            .filter { it != REQUESTED }
            .forEach { notRequestStatus ->
                val service = newSendNotificationCommandService(
                    getSendNotificationLogPort = { _ -> slackSendNotificationLog.copy(status = notRequestStatus) },
                )
                try {
                    service.sendNotification(SendNotificationEvent(sendNotificationLogId = 1))
                    fail()
                } catch (e: PreconditionError) {
                    assertEquals("REQUESTED 상태만 변경 가능합니다. (status: ${notRequestStatus.name})", e.message)
                }
            }
    }
}

private fun newSendNotificationCommandService(
    createSendNotificationLogPort: CreateSendNotificationLogPort = NoErrorSendNotificationAdapter(),
    getSendNotificationLogPort: GetSendNotificationLogPort = NoErrorSendNotificationAdapter(),
    saveSendNotificationLogPort: SaveSendNotificationLogPort = NoErrorSendNotificationAdapter(),
    eventBus: EventBus<SendNotificationEvent> = EventBus(),
    httpApiClient: HttpApiClient = NoErrorHttpClient(),
) = SendNotificationCommandService(
    createSendNotificationLogPort = createSendNotificationLogPort,
    saveSendNotificationLogPort = saveSendNotificationLogPort,
    eventBus = eventBus,
    httpApiClient = httpApiClient,
    getSendNotificationLogPort = getSendNotificationLogPort,
)

//private typealias PostMethodBlock
private fun newHttpClient(postMethodBlock: (String, Any) -> String): NoErrorHttpClient {
    return object : NoErrorHttpClient() {
        override suspend fun <T : Any> _post(
            path: String,
            params: Map<String, String>,
            body: Any,
            headers: Map<String, String>,
            jsonExtraValues: Map<String, Any>,
            clazz: KClass<T>
        ): T {
            return postMethodBlock(path, body) as T
        }
    }
}

open class NoErrorSendNotificationAdapter : SendNotificationLogPort {
    override suspend fun createSendNotificationLog(notificationAppId: Long, requestMessage: String): Long =
        slackSendNotificationLog.sendNotificationLogId

    override suspend fun getSendNotificationLog(sendNotificationLogId: Long): SendNotificationLog? =
        slackSendNotificationLog

    override suspend fun saveSendNotificationLog(domainEntity: SendNotificationLog): Boolean =
        true
}

private open class NoErrorHttpClient : HttpApiClient {
    override suspend fun <T : Any> _post(
        path: String,
        params: Map<String, String>,
        body: Any,
        headers: Map<String, String>,
        jsonExtraValues: Map<String, Any>,
        clazz: KClass<T>
    ): T = "ok" as T

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