package com.newy.algotrade.study.kotlin.coroutine

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TickerTest {
    @OptIn(ObsoleteCoroutinesApi::class)
    @Test
    fun `ticker - 주기적인 체널 파이프라이닝이 필요한 경우 사용`() = runTest {
        val intervalChannel = ticker(
            delayMillis = 10,
            initialDelayMillis = 0,
            coroutineContext
        )

        var counter = 0
        var log = ""
        launch {
            for (signal in intervalChannel) {
                counter++
                log += "request1 "
                delay(1)
            }
        }

        launch {
            for (signal in intervalChannel) {
                counter++
                log += "request2 "
                delay(3)
            }
        }

        delay(35)
        intervalChannel.cancel()
        assertEquals(3, counter)
        assertEquals("request1 request2 request1 ", log)
    }

    @Test
    fun `ticker - coroutineContext 를 전달하지 않으면 구조화된 동시성을 얻지 못함`() = runBlocking {
        // coroutineContext 를 전달하지 않으면, runTest 를 사용할 수 없음
        val intervalChannel = ticker(
            delayMillis = 10,
            initialDelayMillis = 0,
        )

        var counter = 0
        var log = ""
        launch {
            for (signal in intervalChannel) {
                counter++
                log += "request1 "
                delay(1)
            }
        }

        launch {
            for (signal in intervalChannel) {
                counter++
                log += "request2 "
                delay(3)
            }
        }

        delay(35)
        intervalChannel.cancel()
        assertEquals(4, counter, "request2 의 마지막 요청이 취소되지 않는다")
        assertEquals("request1 request2 request1 request2 ", log)
    }
}