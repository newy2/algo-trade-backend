package com.newy.algotrade.study.kotlin.coroutine

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TickerTest {
    @OptIn(ObsoleteCoroutinesApi::class)
    @Test
    fun `ticker - delayMillis 시간 동안 일시정지 되는 랑데뷰 체널`() = runTest {
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
                yield()
            }
        }

        launch {
            for (signal in intervalChannel) {
                counter++
                log += "request2 "
                yield()
            }
        }

        delay(35)
        intervalChannel.cancel()
        assertEquals(3, counter)
        assertEquals("request1 request2 request1 ", log)
    }
}