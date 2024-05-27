package com.newy.algotrade.unit.price2.adapter.out.persistent

import com.newy.algotrade.coroutine_based_application.price2.adpter.out.persistent.InMemoryUserStrategySignalHistoryStore
import com.newy.algotrade.coroutine_based_application.price2.port.out.UserStrategySignalHistoryPort
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.order.OrderSignal
import com.newy.algotrade.domain.chart.order.OrderType
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.test.assertEquals

class InMemoryUserStrategySignalHistoryStoreTest {
    private val signal = OrderSignal(
        OrderType.BUY,
        Candle.TimeRange(
            Duration.ofMinutes(1),
            OffsetDateTime.parse("2024-05-09T00:00+09:00")
        ),
        1000.toBigDecimal()
    )

    private lateinit var store: UserStrategySignalHistoryPort

    @BeforeEach
    fun setUp() {
        store = InMemoryUserStrategySignalHistoryStore().also {
            it.add("id1", signal)
        }
    }

    @Test
    fun `등록한 히스토리 가져오기`() {
        val registeredId = "id1"
        val history = store.get(registeredId)

        history.orders().let {
            assertEquals(1, it.size)
            assertEquals(signal, it.first())
        }
    }

    @Test
    fun `등록하지 않은 히스토리 가져오기`() {
        val unRegisteredId = "id2"
        val history = store.get(unRegisteredId)

        assertTrue(history.isEmpty())
    }

    @Test
    fun `등록한 히스토리 삭제하기`() {
        store.remove("id1")

        val removedId = "id1"
        val history = store.get(removedId)

        assertTrue(history.isEmpty())
    }
}