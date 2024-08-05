package com.newy.algotrade.unit.common.web.http

import com.newy.algotrade.coroutine_based_application.common.web.http.HttpApiRateLimit
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ApiRateLimitTest {
    @Test
    fun `요청 IP 단위로 API 호출 횟수 제한이 걸린 경우`() = runTest {
        val limiter = HttpApiRateLimit(delayMillis = 10, coroutineContext)

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
                delay(1)
            }
        }

        delay(35)
        limiter.cancel()

        assertEquals(3, counter)
        assertEquals("request1 request2 request1 ", log, "총 3번만 실행됨")
    }

    @Test
    fun `계정 별로 API 호출 횟수 제한이 걸린 경우`() = runTest {
        val limiter = HttpApiRateLimit(delayMillis = 10, coroutineContext)

        var counter = 0
        var log = ""
        launch {
            while (isActive) {
                limiter.await(requestId = "user1")
                counter++
                log += "request1 "
                delay(1)
            }
        }

        launch {
            while (isActive) {
                limiter.await(requestId = "user2")
                counter++
                log += "request2 "
                delay(1)
            }
        }

        delay(35)
        limiter.cancel()
        assertEquals(6, counter)
        assertEquals("request1 request2 request1 request2 request1 request2 ", log, "계정 별로 각각 3번씩 실행됨")
    }
}