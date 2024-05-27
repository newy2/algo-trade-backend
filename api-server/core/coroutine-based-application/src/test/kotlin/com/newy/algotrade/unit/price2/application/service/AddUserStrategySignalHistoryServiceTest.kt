package com.newy.algotrade.unit.price2.application.service

import com.newy.algotrade.coroutine_based_application.price2.adpter.out.persistent.InMemoryUserStrategySignalHistoryStore
import com.newy.algotrade.coroutine_based_application.price2.application.service.AddUserStrategySignalHistoryService
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.order.OrderSignal
import com.newy.algotrade.domain.chart.order.OrderType
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.test.assertEquals

class AddUserStrategySignalHistoryServiceTest {
    @Test
    fun `OrderSignal 추가하기`() {
        val store = InMemoryUserStrategySignalHistoryStore()
        val service = AddUserStrategySignalHistoryService(store)
        val signal = OrderSignal(
            OrderType.BUY,
            Candle.TimeRange(
                Duration.ofMinutes(1),
                OffsetDateTime.parse("2024-05-09T00:00+09:00")
            ),
            1000.toBigDecimal()
        )

        service.addHistory("id1", signal)

        assertEquals(signal, store.get("id1").lastOrderSignal())
    }
}