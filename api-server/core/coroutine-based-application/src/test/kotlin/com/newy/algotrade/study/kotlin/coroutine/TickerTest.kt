package com.newy.algotrade.study.kotlin.coroutine

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

        delay(39)
        intervalChannel.cancel()
        assertEquals(3, counter)
        assertEquals("request1 request2 request1 ", log)
    }
}