package com.newy.algotrade.unit.notification.adapter.out.external_system

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.common.exception.HttpResponseException
import com.newy.algotrade.common.mapper.JsonConverterByJackson
import com.newy.algotrade.common.web.default_implement.DefaultHttpApiClient
import com.newy.algotrade.notification.adapter.out.external_system.SendNotificationMessageAdapter
import com.newy.algotrade.notification.domain.NotificationApp
import com.newy.algotrade.notification.domain.NotificationSendMessage
import com.newy.algotrade.notification.domain.Webhook
import helpers.TestServerPort
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals

@DisplayName("슬렉 메세지 전송 API 호출 테스트")
class SendNotificationMessageAdapterTest {
    private lateinit var adapter: SendNotificationMessageAdapter
    private lateinit var server: MockWebServer
    private val message = NotificationSendMessage(
        notificationApp = NotificationApp(
            id = 1,
            webhook = Webhook.from(
                type = "SLACK",
                url = "https://hooks.slack.com/services/1111",
            )
        ),
        requestMessage = "test message"
    )

    @BeforeEach
    fun setUp() {
        val port = TestServerPort.nextValue()
        adapter = SendNotificationMessageAdapter(
            slackApiClient = DefaultHttpApiClient(
                OkHttpClient(),
                "http://localhost:$port",
                JsonConverterByJackson(jacksonObjectMapper()),
            )
        )
        server = MockWebServer().also {
            it.start(port)
        }
    }

    @AfterEach
    fun tearDown() {
        server.close()
    }

    @Test
    fun `슬렉 메세지 전송 API 호출 시, url path 와 request body 는 아래와 같다`() = runBlocking {
        server.enqueue(MockResponse().setBody("ok"))

        adapter.sendMessage(message)

        server.takeRequest().let {
            assertEquals("/services/1111", it.path, "URL Path")
            assertEquals(
                """{"blocks":[{"type":"section","text":{"type":"mrkdwn","text":"test message"}}]}""",
                it.body.readUtf8(),
                "Request Body",
            )
        }
    }

    @Test
    fun `슬렉 메세지 전송 API 가 성공하면 response body 를 리턴한다`() = runBlocking {
        server.enqueue(MockResponse().setBody("ok"))

        val response = adapter.sendMessage(message)

        assertEquals("ok", response)
    }

    @Test
    fun `슬렉 메세지 전송 API 가 성공하면 HttpResponseException 에러가 발생한다`() = runBlocking {
        server.enqueue(MockResponse().setBody("not ok"))

        val error = assertThrows<HttpResponseException> {
            adapter.sendMessage(message)
        }

        assertEquals("not ok", error.responseMessage)
    }
}