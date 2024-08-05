package com.newy.algotrade.unit.chart.strategy

import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.order.OrderType
import com.newy.algotrade.domain.chart.strategy.StrategySignal
import com.newy.algotrade.domain.chart.strategy.StrategySignalHistory
import com.newy.algotrade.domain.chart.strategy.TrafficLight
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime

private fun strategySignal(orderType: OrderType, amount: Number) =
    StrategySignal(
        orderType,
        Candle.TimeRange(
            Duration.ofMinutes(1),
            OffsetDateTime.parse("2024-05-09T00:00+09:00")
        ),
        amount.toDouble().toBigDecimal()
    )

class ZeroSizeTrafficLightTest {
    private val trafficLight = TrafficLight(checkSignalPairSize = 0)

    @Test
    fun `과거 거래 내역이 없었던 경우`() {
        val noHistories = StrategySignalHistory()
        assertTrue(trafficLight.isGreen(noHistories))
    }

    @Test
    fun `진입 신호만 있는 경우`() {
        val openedHistory = StrategySignalHistory().also {
            it.add(strategySignal(OrderType.BUY, 1000))
        }

        assertTrue(trafficLight.isGreen(openedHistory))
    }

    @Test
    fun `진입, 진출 신호가 1쌍이 있는 경우`() {
        val openedHistory = StrategySignalHistory().also {
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1000))
        }

        assertTrue(trafficLight.isGreen(openedHistory))
    }
}

class OneSizeTrafficLightTest {
    private val trafficLight = TrafficLight(checkSignalPairSize = 1)

    @Test
    fun `거래 내역이 없는 경우`() {
        val noHistories = StrategySignalHistory()
        assertTrue(trafficLight.isGreen(noHistories))
    }

    @Test
    fun `진입 신호만 있는 경우`() {
        val openedHistory = StrategySignalHistory().also {
            it.add(strategySignal(OrderType.BUY, 1000))
        }

        assertTrue(trafficLight.isGreen(openedHistory), "진입 신호만 있는 경우")
    }

    @Test
    fun `진입, 진출 신호가 1쌍이 있는 경우`() {
        StrategySignalHistory().also {
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1100))
        }.let { winHistory ->
            assertTrue(trafficLight.isGreen(winHistory))
        }

        StrategySignalHistory().also {
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1000))
        }.let { drawHistory ->
            assertFalse(trafficLight.isGreen(drawHistory))
        }

        StrategySignalHistory().also {
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 900))
        }.let { loseHistory ->
            assertFalse(trafficLight.isGreen(loseHistory))
        }
    }

    @Test
    fun `history 의 진입,진출 쌍의 개수가 checkSignalPairSize 값 보다 큰 경우 - checkSignalPairSize 는 history 의 가장 마지막 데이터 기준으로 계산한다`() {
        StrategySignalHistory().also {
            // 과거 주문(Loss)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 900))

            // 과거 주문(Loss)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 900))

            // 최근 주문(Win)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1100))
        }.let { winHistory ->
            assertTrue(trafficLight.isGreen(winHistory))
        }

        StrategySignalHistory().also {
            // 과거 주문(Win)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1100))

            // 과거 주문(Loss)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 900))

            // 최근 주문(Draw)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1000))
        }.let { drawHistory ->
            assertFalse(trafficLight.isGreen(drawHistory))
        }

        StrategySignalHistory().also {
            // 과거 주문(Draw)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1000))

            // 과거 주문(Win)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1100))

            // 최근 주문(Loss)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 900))
        }.let { loseHistory ->
            assertFalse(trafficLight.isGreen(loseHistory))
        }
    }
}