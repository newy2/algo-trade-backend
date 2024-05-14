package com.newy.algotrade.unit.common.coroutine

import com.newy.algotrade.coroutine_based_application.common.coroutine.PollingJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.coroutines.CoroutineContext

class StringPollingJob(
    delayMillis: Long,
    coroutineContext: CoroutineContext,
    callback: suspend (Pair<String, String>) -> Unit,
) : PollingJob<String, String>(delayMillis, coroutineContext, callback) {
    companion object {
        suspend fun startWith(
            delayMillis: Long,
            coroutineContext: CoroutineContext,
            callback: suspend (Pair<String, String>) -> Unit,
        ): StringPollingJob {
            return StringPollingJob(delayMillis, coroutineContext, callback).also { it.start() }
        }
    }

    override suspend fun eachProcess(data: String) = data
}

class PollingTest {
    @Test
    fun `첫 번째 요청은 바로 시작됨`() = runBlocking {
        var log = ""
        val polling = StringPollingJob.startWith(
            10,
            coroutineContext
        ) {
            log += "${it.second} "
        }

        polling.subscribe("a")
        polling.subscribe("b")

        delay(5)
        polling.cancel()

        assertEquals("a ", log)
    }

    @Test
    fun `같은 data 로 여러번 subscribe 하는 경우`() = runBlocking {
        var log = ""
        val polling = StringPollingJob.startWith(
            10,
            coroutineContext
        ) {
            log += "${it.second} "
        }

        polling.subscribe("a")
        polling.subscribe("a")
        polling.subscribe("a")
        polling.subscribe("b")

        delay(15)
        polling.cancel()

        assertEquals("a b ", log)
    }

    @Test
    fun `unSubscribe 한 경우`() = runBlocking {
        var log = ""
        val polling = StringPollingJob.startWith(
            10,
            coroutineContext
        ) {
            log += "${it.second} "
        }

        polling.subscribe("a")
        polling.subscribe("b")
        delay(1)
        polling.unSubscribe("a")

        delay(25)
        polling.cancel()
        assertEquals("a b b ", log)
    }

    @Test
    fun `2세트 폴링 테스트`() = runBlocking {
        var log = ""
        val polling = StringPollingJob.startWith(
            10,
            coroutineContext
        ) {
            log += "${it.second} "
        }

        polling.subscribe("a")
        polling.subscribe("b")

        delay(35)
        polling.cancel()
        assertEquals("a b a b ", log)
    }
}