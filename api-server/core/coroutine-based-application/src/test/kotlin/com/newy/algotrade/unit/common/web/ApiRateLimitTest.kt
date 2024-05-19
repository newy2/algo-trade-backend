package com.newy.algotrade.unit.common.web

import com.newy.algotrade.coroutine_based_application.common.web.HttpApiRateLimit
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ApiRateLimitTest {
    @Test
    fun `외부 API 초당 최대 호출 제한`() = runTest {
        val limiter = HttpApiRateLimit(
            delayMillis = 10,
            coroutineContext
        )

        var counter = 0
        var log = ""
        launch {
            while (isActive) {
                limiter.await()
                counter++
                log += "request1 "
                delay(1)
            }
        }

        launch {
            while (isActive) {
                limiter.await()
                counter++
                log += "request2 "
                delay(3)
            }
        }

        delay(39)
        limiter.cancel()
        assertEquals(3, counter)
        assertEquals("request1 request2 request1 ", log)
    }

    @Test
    fun `계정별로 초당 호출 제한이 있는 경우`() = runTest {
        val limiter = HttpApiRateLimit(
            delayMillis = 10,
            coroutineContext
        )

        var counter = 0
        var log = ""
        launch {
            while (isActive) {
                limiter.await("user1")
                counter++
                log += "request1 "
                delay(1)
            }
        }

        launch {
            while (isActive) {
                limiter.await("user2")
                counter++
                log += "request2 "
                delay(3)
            }
        }

        delay(39)
        limiter.cancel()
        assertEquals(6, counter)
        assertEquals("request1 request2 request1 request2 request1 request2 ", log)
    }
}