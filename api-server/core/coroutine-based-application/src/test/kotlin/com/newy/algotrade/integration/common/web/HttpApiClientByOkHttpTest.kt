package com.newy.algotrade.integration.common.web

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.coroutine_based_application.common.web.HttpApiClient
import com.newy.algotrade.coroutine_based_application.common.web.get
import com.newy.algotrade.coroutine_based_application.common.web.post
import helpers.HttpApiClientByOkHttp
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.*

open class BaseTest {
    protected lateinit var client: HttpApiClient
    protected lateinit var server: MockWebServer

    @BeforeEach
    fun setUp() {
        val port = 9991
        client = HttpApiClientByOkHttp(
            OkHttpClient(),
            "http://localhost:$port",
            jacksonObjectMapper(),
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
            Assertions.assertEquals("application/json", it.headers["Content-Type"])
            Assertions.assertEquals("1", it.headers["X-Custom-Header"])
        }
    }

    @Test
    fun `Response 를 String 으로 파싱하기`() = runBlocking {
        Assertions.assertEquals("""{"key":1,"value":"a"}""", client.get<String>(path = "/path"))
    }

    @Test
    fun `Response 를 Object 로 파싱하기`() = runBlocking {
        Assertions.assertEquals(SimpleData(key = 1, value = "a"), client.get<SimpleData>(path = "/path"))
    }

    @Test
    fun `Response 무시하기`() = runBlocking {
        Assertions.assertEquals(Unit, client.get<Unit>(path = "/path"))
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

        Assertions.assertEquals("/path?category=spot&symbol=BTC", server.takeRequest().path)
    }

    @Test
    fun `POST - 전달된 JSON body 확인하기`() = runBlocking {
        client.post<Unit>(
            path = "/path",
            body = SimpleData(key = 2, value = "b")
        )

        Assertions.assertEquals("""{"key":2,"value":"b"}""", server.takeRequest().body.readUtf8())
    }
}