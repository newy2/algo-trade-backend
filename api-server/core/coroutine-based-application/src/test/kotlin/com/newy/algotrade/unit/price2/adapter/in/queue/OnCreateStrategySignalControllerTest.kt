package com.newy.algotrade.unit.price2.adapter.`in`.queue

import com.newy.algotrade.coroutine_based_application.price2.adapter.`in`.queue.OnCreateStrategySignalController
import com.newy.algotrade.coroutine_based_application.price2.port.`in`.AddStrategySignalHistoryUseCase
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.order.OrderSignal
import com.newy.algotrade.domain.chart.order.OrderType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime

class OnCreateStrategySignalControllerTest : AddStrategySignalHistoryUseCase {
    private var log: String = ""
    override fun addHistory(userStrategyId: String, signal: OrderSignal) {
        log += "addHistory "
    }

    // TODO RDB 업데이트
    // TODO 메신저 알림 API 호출
    // TODO 주문 API 호출

    @Test
    fun `UseCase 호출 순서 확인`() {
        val controller = OnCreateStrategySignalController(this)
        val signal = OrderSignal(
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