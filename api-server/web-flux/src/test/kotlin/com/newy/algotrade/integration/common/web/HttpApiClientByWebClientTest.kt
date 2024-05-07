package com.newy.algotrade.integration.common.web

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.coroutine_based_application.common.web.FormData
import com.newy.algotrade.coroutine_based_application.common.web.HttpApiClient
import com.newy.algotrade.coroutine_based_application.common.web.get
import com.newy.algotrade.coroutine_based_application.common.web.post
import com.newy.algotrade.domain.common.mapper.JsonConverterByJackson
import com.newy.algotrade.web_flux.common.web.HttpApiClientByWebClient
import helpers.TestServerPort
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.web.reactive.function.client.WebClient

// TODO 테스트 코드 중복 제거(-> HttpApiClientTest)
open class BaseTest {
    private val port = TestServerPort.nextValue()
    protected lateinit var client: HttpApiClient
    protected lateinit var server: MockWebServer

    @BeforeEach
    fun setUp() {
        client = HttpApiClientByWebClient(
            WebClient.builder().build(),
            "http://localhost:$port",
            JsonConverterByJackson(jacksonObjectMapper()),
        )
        server = MockWebServer().also {
            it.start(port)
            it.enqueue(
                MockResponse()
                    .setHeader("Content-Type", "application/json")
                    .setBody("""{"key":1,"value":"a"}""")
            )
        }
    }

    @AfterEach
    fun tearDown() {
        server.close()
    }
}

data class SimpleData(val key: Int, val value: String)

@DisplayName("공통 기능 테스트")
class CommonFunctionTest : BaseTest() {
    @Test
    fun `header 설정 하기`() = runBlocking {
        client.get<Unit>(
            path = "/path",
            headers = mapOf(
                "Content-Type" to "application/json",
                "X-Custom-Header" to "1",
            )
        )

        server.takeRequest().let {
            assertEquals("application/json", it.headers["Content-Type"])
            assertEquals("1", it.headers["X-Custom-Header"])
        }
    }

    @Test
    fun `Response 무시하기`() = runBlocking {
        val parsed = client.get<Unit>(path = "/path")
        assertEquals(Unit, parsed)
    }

    @Test
    fun `Response 를 String 으로 파싱하기`() = runBlocking {
        val parsed = client.get<String>(path = "/path")
        assertEquals("""{"key":1,"value":"a"}""", parsed)
    }

    @Test
    fun `Response 를 Object 로 파싱하기`() = runBlocking {
        val parsed = client.get<SimpleData>(path = "/path")
        assertEquals(SimpleData(key = 1, value = "a"), parsed)
    }

    @Test
    fun `Response 의 JSON 과 jsonExtraValue 를 더해서 Object 로 파싱하기`() = runBlocking {
        data class ExtraClass(
            val key: Int,
            val value: String,
            @JacksonInject("extraValue") val extraValue: String
        )

        val parsed = client.get<ExtraClass>(
            path = "/path",
            jsonExtraValues = mapOf("extraValue" to "b2"),
        )

        assertEquals(ExtraClass(key = 1, value = "a", extraValue = "b2"), parsed)
    }
}

@DisplayName("HTTP Method 테스트")
class HttpMethodTest : BaseTest() {
    @Test
    fun `GET - 생성된 Query Params 확인하기`() = runBlocking {
        client.get<Unit>(
            path = "/path",
            params = mapOf(
                "category" to "spot",
                "symbol" to "BTC",
            )
        )

        assertEquals("/path?category=spot&symbol=BTC", server.takeRequest().path)
    }

    @Test
    fun `POST - 전달된 JSON body 확인하기`() = runBlocking {
        client.post<Unit>(
            path = "/path",
            body = SimpleData(key = 2, value = "b")
        )

        assertEquals("""{"key":2,"value":"b"}""", server.takeRequest().body.readUtf8())
    }

    @Test
    fun `POST - 전달된 Form body 확인하기`() = runBlocking {
        client.post<Unit>(
            path = "/path",
            headers = mapOf("Content-type" to "application/x-www-form-urlencoded"),
            body = FormData(Pair("value", "a"))
        )

        assertEquals("value=a", server.takeRequest().body.readUtf8())
    }
}