package com.newy.algotrade.unit.price2.application.service.strategy

import com.newy.algotrade.coroutine_based_application.price2.adapter.out.persistent.InMemoryStrategySignalHistoryStore
import com.newy.algotrade.coroutine_based_application.price2.application.service.strategy.AddStrategySignalHistoryService
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.order.OrderType
import com.newy.algotrade.domain.chart.strategy.StrategySignal
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.test.assertEquals

class AddStrategySignalHistoryServiceTest {
    @Test
    fun `OrderSignal 추가하기`() {
        val store = InMemoryStrategySignalHistoryStore()
        val service = AddStrategySignalHistoryService(store)
        val signal = StrategySignal(
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