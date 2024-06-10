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
    private val trafficLight = TrafficLight(0)

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
    private val trafficLight = TrafficLight(1)

    @Test
    fun `거래 내역이 없는 경우`() {
        val noHistories = StrategySignalHistory()
        assertTrue(trafficLight.isGreen(noHistories))
    }

    @Test
    fun `진입 신호만 있는 경우`() {
        assertTrue(trafficLight.isGreen(StrategySignalHistory().also {
            it.add(strategySignal(OrderType.BUY, 1000))
        }), "진입 신호만 있는 경우")
    }

    @Test
    fun `진입, 진출 신호가 1쌍이 있는 경우`() {
        assertTrue(trafficLight.isGreen(StrategySignalHistory().also {
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1100))
        }), "Win 주문인 경우")

        assertFalse(trafficLight.isGreen(StrategySignalHistory().also {
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1000))
        }), "Draw 주문인 경우")

        assertFalse(trafficLight.isGreen(StrategySignalHistory().also {
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 900))
        }), "Loss 주문인 경우")
    }

    @Test
    fun `진입, 진출 신호가 2쌍이 있는 경우`() {
        assertTrue(trafficLight.isGreen(StrategySignalHistory().also {
            // 과거 주문(Draw)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1000))

            // 최근 주문(Win)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1100))
        }), "Win 주문인 경우")

        assertFalse(trafficLight.isGreen(StrategySignalHistory().also {
            // 과거 주문(Draw)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1000))

            // 최근 주문(Draw)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1000))
        }), "Draw 주문인 경우")

        assertFalse(trafficLight.isGreen(StrategySignalHistory().also {
            // 과거 주문(Draw)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1000))

            // 최근 주문(Loss)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 900))
        }), "Loss 주문인 경우")
    }
}

class TwoSizeTrafficLightTest {
    private val trafficLight = TrafficLight(2)

    @Test
    fun `거래 내역이 없는 경우`() {
        val noHistories = StrategySignalHistory()
        assertTrue(trafficLight.isGreen(noHistories))
    }

    @Test
    fun `진입 신호만 있는 경우`() {
        assertTrue(trafficLight.isGreen(StrategySignalHistory().also {
            it.add(strategySignal(OrderType.BUY, 1000))
        }), "진입 신호만 있는 경우")
    }

    @Test
    fun `진입, 진출 신호가 1쌍이 있는 경우`() {
        assertTrue(trafficLight.isGreen(StrategySignalHistory().also {
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1100))
        }), "Win 주문인 경우")

        assertFalse(trafficLight.isGreen(StrategySignalHistory().also {
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1000))
        }), "Draw 주문인 경우")

        assertFalse(trafficLight.isGreen(StrategySignalHistory().also {
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 900))
        }), "Loss 주문인 경우")
    }

    @Test
    fun `진입, 진출 신호가 2쌍이 있는 경우`() {
        assertTrue(trafficLight.isGreen(StrategySignalHistory().also {
            // 과거 주문(Win)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1100))

            // 최근 주문(Win)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1100))
        }), "Win 주문이 2개인 경우")

        assertFalse(trafficLight.isGreen(StrategySignalHistory().also {
            // 과거 주문(Draw)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1000))

            // 최근 주문(Win)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1100))
        }), "Draw 주문 1개, Win 주문 1개인 경우")

        assertFalse(trafficLight.isGreen(StrategySignalHistory().also {
            // 과거 주문(Loss)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 900))

            // 최근 주문(Win)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1100))
        }), "Loss 주문 1개, Win 주문 1개인 경우")
    }
}

class ThreeSizeTrafficLightTest {
    private val trafficLight = TrafficLight(3)

    @Test
    fun `거래 내역이 없는 경우`() {
        val noHistories = StrategySignalHistory()
        assertTrue(trafficLight.isGreen(noHistories))
    }

    @Test
    fun `진입 신호만 있는 경우`() {
        assertTrue(trafficLight.isGreen(StrategySignalHistory().also {
            it.add(strategySignal(OrderType.BUY, 1000))
        }), "진입 신호만 있는 경우")
    }

    @Test
    fun `진입, 진출 신호가 1쌍이 있는 경우`() {
        assertTrue(trafficLight.isGreen(StrategySignalHistory().also {
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1100))
        }), "Win 주문인 경우")

        assertTrue(trafficLight.isGreen(StrategySignalHistory().also {
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1000))
        }), "Draw 주문인 경우")

        assertTrue(trafficLight.isGreen(StrategySignalHistory().also {
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 900))
        }), "Loss 주문인 경우")
    }

    @Test
    fun `진입, 진출 신호가 2쌍이 있는 경우`() {
        assertTrue(trafficLight.isGreen(StrategySignalHistory().also {
            // 과거 주문(Win)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1100))

            // 최근 주문(Win)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1100))
        }), "Win 주문이 2개인 경우")

        assertTrue(trafficLight.isGreen(StrategySignalHistory().also {
            // 과거 주문(Draw)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1000))

            // 최근 주문(Win)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1100))
        }), "Draw 주문 1개, Win 주문 1개인 경우")

        assertTrue(trafficLight.isGreen(StrategySignalHistory().also {
            // 과거 주문(Loss)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 900))

            // 최근 주문(Win)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1100))
        }), "Loss 주문 1개, Win 주문 1개인 경우")

        assertFalse(trafficLight.isGreen(StrategySignalHistory().also {
            // 과거 주문(Draw)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1000))

            // 최근 주문(Draw)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 1000))
        }), "Draw 주문 2개인 경우")

        assertFalse(trafficLight.isGreen(StrategySignalHistory().also {
            // 과거 주문(Loss)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 900))

            // 최근 주문(Win)
            it.add(strategySignal(OrderType.BUY, 1000))
            it.add(strategySignal(OrderType.SELL, 900))
        }), "Loss 주문 2개인 경우")
    }
}