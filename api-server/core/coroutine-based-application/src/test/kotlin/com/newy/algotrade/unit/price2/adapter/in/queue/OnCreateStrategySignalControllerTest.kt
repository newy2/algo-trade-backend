package com.newy.algotrade.unit.price2.adapter.`in`.queue

import com.newy.algotrade.coroutine_based_application.price2.adapter.`in`.queue.OnCreateStrategySignalController
import com.newy.algotrade.coroutine_based_application.price2.port.`in`.strategy.AddStrategySignalHistoryUseCase
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.order.OrderType
import com.newy.algotrade.domain.chart.strategy.StrategySignal
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime

class OnCreateStrategySignalControllerTest : AddStrategySignalHistoryUseCase {
    private var log: String = ""
    override suspend fun addHistory(userStrategyId: String, signal: StrategySignal) {
        log += "addHistory "
    }

    // TODO RDB 업데이트
    // TODO 메신저 알림 API 호출
    // TODO 주문 API 호출

    @Test
    fun `UseCase 호출 순서 확인`() = runTest {
        val controller = OnCreateStrategySignalController(this@OnCreateStrategySignalControllerTest)
        val signal = StrategySignal(
            OrderType.BUY,
            Candle.TimeRange(
                Duration.ofMinutes(1),
                OffsetDateTime.parse("2024-05-09T00:00+09:00")
            ),
            1000.toBigDecimal()
        )

        controller.onCreateSignal("id1", signal)

        Assertions.assertEquals("addHistory ", log)
    }
}