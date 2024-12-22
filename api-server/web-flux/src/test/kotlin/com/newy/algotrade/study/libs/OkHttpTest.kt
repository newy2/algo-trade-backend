package com.newy.algotrade.study.libs

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.common.mapper.JsonConverterByJackson
import com.newy.algotrade.common.web.default_implement.awaitCall
import helpers.TestServerPort
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OkHttpTest {
    private val jsonConverter = JsonConverterByJackson(jacksonObjectMapper())
    private val client = OkHttpClient()
    private lateinit var baseRequest: Request
    private lateinit var server: MockWebServer

    @BeforeEach
    fun setUp() {
        val port = TestServerPort.nextValue()
        baseRequest = Request.Builder()
            .url("http://localhost:$port")
            .headers(
                Headers.headersOf(
                    "Content-Type", "application/json",
                    "X-Custom-Header", "1"
                )
            )
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
        client.newCall(baseRequest).awaitCall()

        server.takeRequest().let {
            assertEquals("application/json", it.headers["Content-Type"])
            assertEquals("1", it.headers["X-Custom-Header"])
        }
    }

    @Test
    fun `기본 request header 값 오버라이딩`() = runBlocking {
        val overrideHeadersRequest = baseRequest.newBuilder()
            .header("X-Custom-Header", "2")
            .header("X-Other-Custom-Header", "abc")
            .build()

        client.newCall(overrideHeadersRequest).awaitCall()

        server.takeRequest().let {
            assertEquals("application/json", it.headers["Content-Type"])
            assertEquals("2", it.headers["X-Custom-Header"])
            assertEquals("abc", it.headers["X-Other-Custom-Header"])
        }
    }

    @Test
    fun `GET - Query Params 생성하기`() = runBlocking {
        val request = baseRequest.newBuilder()
            .get()
            .url(
                baseRequest.url
                    .newBuilder()
                    .encodedPath("/path")
                    .addQueryParameter("category", "spot")
                    .addQueryParameter("symbol", "BTCUSDT")
                    .build()
            )
            .build()

        client.newCall(request).awaitCall()

        assertEquals("/path?category=spot&symbol=BTCUSDT", server.takeRequest().path)
    }

    @Test
    fun `GET - 동적으로 Query Params 생성하기`() = runBlocking {
        val map = mapOf(
            "category" to "spot",
            "symbol" to "BTCUSDT",
        )

        val request = baseRequest.newBuilder()
            .get()
            .url(
                baseRequest.url
                    .newBuilder()
                    .encodedPath("/path").also {
                        map.forEach { (key, value) ->
                            it.addQueryParameter(key, value)
                        }
                    }
                    .build()
            )
            .build()

        client.newCall(request).awaitCall()

        assertEquals("/path?category=spot&symbol=BTCUSDT", server.takeRequest().path)
    }

    class SimpleData(val key: Int, val value: String)

    @Test
    fun `POST - JSON body 전달하기`() = runBlocking {
        val body = SimpleData(key = 1, value = "a")

        val request = baseRequest.newBuilder()
            .post(
                jsonConverter.toJson(body).toRequestBody(
                    "application/json; charset=utf-8".toMediaType()
                )
            )
            .url(
                baseRequest.url
                    .newBuilder()
                    .encodedPath("/path")
                    .build()
            )
            .build()

        client.newCall(request).awaitCall()

        assertEquals("""{"key":1,"value":"a"}""", server.takeRequest().body.readUtf8())
    }

    @Test
    fun `POST - Form body 전달하기`() = runBlocking {
        val request = baseRequest.newBuilder()
            .url(
                baseRequest.url
                    .newBuilder()
                    .encodedPath("/path")
                    .build()
            )
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .post(FormBody.Builder().addEncoded("value", "a").build())
            .build()

        client.newCall(request).awaitCall()

        assertEquals("value=a", server.takeRequest().body.readUtf8())
    }
}