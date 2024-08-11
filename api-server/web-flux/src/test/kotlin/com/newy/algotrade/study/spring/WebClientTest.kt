package com.newy.algotrade.study.spring

import helpers.TestServerPort
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodyOrNull

class WebClientTest {
    private lateinit var client: WebClient
    private lateinit var server: MockWebServer

    @BeforeEach
    fun setUp() {
        val port = TestServerPort.nextValue()
        client = WebClient.builder()
            .baseUrl("http://localhost:$port")
            .defaultHeaders {
                it.contentType = MediaType.APPLICATION_JSON
                it["X-Custom-Header"] = "1"
            }
            .build()
        server = MockWebServer().also {
            it.start(port)
            it.enqueue(MockResponse().setBody("OK"))
        }
    }

    @AfterEach
    fun tearDown() {
        server.close()
    }

    @Test
    fun `기본 header 값 확인`() = runBlocking {
        client
            .get()
            .uri("/path")
            .retrieve()
            .awaitBodyOrNull<Unit>()

        server.takeRequest().let {
            assertEquals("application/json", it.headers["Content-Type"])
            assertEquals("1", it.headers["X-Custom-Header"])
        }
    }

    @Test
    fun `기본 header 값 오버라이딩`() = runBlocking {
        client
            .get()
            .uri("/path")
            .headers {
                it["X-Custom-Header"] = "2"
                it["X-Other-Custom-Header"] = "abc"
            }
            .retrieve()
            .awaitBodyOrNull<Unit>()

        server.takeRequest().let {
            assertEquals("application/json", it.headers["Content-Type"])
            assertEquals("2", it.headers["X-Custom-Header"])
            assertEquals("abc", it.headers["X-Other-Custom-Header"])
        }
    }

    @Test
    fun `GET - Query Params 생성하기`() = runBlocking {
        client
            .get()
            .uri {
                it.path("/path")
                    .queryParam("category", "spot")
                    .queryParam("symbol", "BTCUSDT")
                    .build()
            }
            .retrieve()
            .awaitBodyOrNull<Unit>()

        assertEquals("/path?category=spot&symbol=BTCUSDT", server.takeRequest().path)
    }

    @Test
    fun `GET - 동적으로 Query Params 생성하기`() = runBlocking {
        val map = mapOf(
            "category" to "spot",
            "symbol" to "BTCUSDT",
        )

        client
            .get()
            .uri {
                it.path("/path").also {
                    map.forEach { (key, value) ->
                        it.queryParam(key, value)
                    }
                }.build()
            }
            .retrieve()
            .awaitBodyOrNull<Unit>()

        assertEquals("/path?category=spot&symbol=BTCUSDT", server.takeRequest().path)
    }

    class SimpleData(val key: Int, val value: String)

    @Test
    fun `POST - JSON body 전달하기`() = runBlocking {
        val body = SimpleData(key = 1, value = "a")

        client
            .post()
            .uri("/path")
            .bodyValue(body)
            .retrieve()
            .awaitBodyOrNull<Unit>()

        assertEquals("""{"key":1,"value":"a"}""", server.takeRequest().body.readUtf8())
    }

    @Test
    fun `POST - Form body 전달하기`() = runBlocking {
        client
            .post()
            .uri("/path")
            .header("Content-type", "application/x-www-form-urlencoded")
            .body(BodyInserters.fromFormData("value", "a"))
            .retrieve()
            .awaitBodyOrNull<Unit>()

        assertEquals("value=a", server.takeRequest().body.readUtf8())
    }
}