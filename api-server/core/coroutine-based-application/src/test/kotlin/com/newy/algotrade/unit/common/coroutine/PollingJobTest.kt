package com.newy.algotrade.unit.common.coroutine

import com.newy.algotrade.coroutine_based_application.common.coroutine.PollingJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
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

    override suspend fun eachProcess(key: String) = key
}

class PollingTest {
    private var log = ""

    @BeforeEach
    fun setUp() {
        log = ""
    }

    @Test
    fun `delayMillis 가 지나기 전에, 첫 번째 요청은 바로 시작된다`() = runBlocking {
        val polling = StringPollingJob.startWith(delayMillis = 10, coroutineContext) { (key, value) ->
            log += "$key "
        }

        polling.subscribe(key = "BTCUSDT")
        polling.subscribe(key = "ETHUSDT")

        delay(5)
        polling.cancel()

        assertEquals("BTCUSDT ", log)
    }

    @Test
    fun `delayMillis 가 지나면, 다음 subscribe 작업이 실행된다`() = runBlocking {
        val polling = StringPollingJob.startWith(delayMillis = 10, coroutineContext) { (key, value) ->
            log += "$key "
        }

        polling.subscribe(key = "BTCUSDT")
        polling.subscribe(key = "ETHUSDT")

        delay(15)
        polling.cancel()

        assertEquals("BTCUSDT ETHUSDT ", log)
    }

    @Test
    fun `한 번 더 delayMillis 가 지나면, subscribe 작업은 롤링된다`() = runBlocking {
        val polling = StringPollingJob.startWith(delayMillis = 10, coroutineContext) { (key, value) ->
            log += "$key "
        }

        polling.subscribe(key = "BTCUSDT")
        polling.subscribe(key = "ETHUSDT")

        delay(25)
        polling.cancel()

        assertEquals("BTCUSDT ETHUSDT BTCUSDT ", log)
    }

    @Test
    fun `subscribe 는 중복 key 를 허용하지 않는다 - 여러 번 등록해도 한번씩 실행된다`() = runBlocking {
        val polling = StringPollingJob.startWith(delayMillis = 10, coroutineContext) { (key, value) ->
            log += "$key "
        }

        repeat(2) {
            polling.subscribe(key = "BTCUSDT")
        }
        polling.subscribe(key = "ETHUSDT")

        delay(18)
        polling.cancel()

        assertEquals("BTCUSDT ETHUSDT ", log)
    }

    @Test
    fun `unSubscribe 한 경우`() = runBlocking {
        val polling = StringPollingJob.startWith(delayMillis = 10, coroutineContext) { (key, value) ->
            log += "$key "
        }

        polling.subscribe("BTCUSDT")
        polling.subscribe("ETHUSDT")

        delay(1)
        polling.unSubscribe("BTCUSDT")

        delay(25)
        polling.cancel()

        assertEquals("BTCUSDT ETHUSDT ETHUSDT ", log)
    }
}