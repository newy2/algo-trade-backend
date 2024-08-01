package com.newy.algotrade.unit.back_testing.adapter.`in`.web

import com.newy.algotrade.coroutine_based_application.back_testing.adapter.`in`.web.RunBackTestingController
import com.newy.algotrade.coroutine_based_application.back_testing.port.`in`.model.BackTestingDataKey
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.order.OrderType
import com.newy.algotrade.domain.chart.strategy.StrategySignal
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.test.assertEquals

class RunBackTestingControllerTest {
    @Test
    fun `트리플 RSI 버전1 백테스팅`() = runTest {
        val controller = RunBackTestingController()
        val backTestingDataKey = BackTestingDataKey(
            ProductPriceKey(
                Market.BY_BIT,
                ProductType.SPOT,
                "BTCUSDT",
                Duration.ofMinutes(1),
            ),
            OffsetDateTime.parse("2024-06-01T00:00+09:00"),
            OffsetDateTime.parse("2024-06-05T00:00+09:00"),
        )
        val strategyClassName = "BuyTripleRSIStrategy"

        val strategySignalHistory = controller.runBackTesting(backTestingDataKey, strategyClassName)

        assertEquals(
            listOf(
                StrategySignal(
                    OrderType.BUY,
                    Candle.TimeRange(
                        Duration.ofMinutes(1),
                        OffsetDateTime.parse("2024-06-01T02:20+09:00")
                    ),
                    67381.86.toBigDecimal()
                ),
                StrategySignal(
                    OrderType.SELL,
                    Candle.TimeRange(
                        Duration.ofMinutes(1),
                        OffsetDateTime.parse("2024-06-03T15:29+09:00")
                    ),
                    69102.0.toBigDecimal()
                ),
                StrategySignal(
                    OrderType.BUY,
                    Candle.TimeRange(
                        Duration.ofMinutes(1),
                        OffsetDateTime.parse("2024-06-03T15:38+09:00")
                    ),
                    69171.53.toBigDecimal()
                ),
            ),
            strategySignalHistory.strategySignals()
        )
    }

    @Test
    fun `트리플 RSI 버전2 백테스팅`() = runTest {
        val controller = RunBackTestingController()
        val backTestingDataKey = BackTestingDataKey(
            ProductPriceKey(
                Market.BY_BIT,
                ProductType.SPOT,
                "BTCUSDT",
                Duration.ofMinutes(1),
            ),
            OffsetDateTime.parse("2024-06-01T00:00+09:00"),
            OffsetDateTime.parse("2024-06-05T00:00+09:00"),
        )
        val strategyClassName = "BuyTripleRSIStrategyV2"

        val strategySignalHistory = controller.runBackTesting(backTestingDataKey, strategyClassName)

        assertEquals(
            listOf(
                StrategySignal(
                    OrderType.BUY,
                    Candle.TimeRange(
                        Duration.ofMinutes(1),
                        OffsetDateTime.parse("2024-06-01T01:32+09:00")
                    ),
                    67050.6.toBigDecimal()
                ),
                StrategySignal(
                    OrderType.SELL,
                    Candle.TimeRange(
                        Duration.ofMinutes(1),
                        OffsetDateTime.parse("2024-06-03T11:17+09:00")
                    ),
                    68768.99.toBigDecimal()
                ),
                StrategySignal(
                    OrderType.BUY,
                    Candle.TimeRange(
                        Duration.ofMinutes(1),
                        OffsetDateTime.parse("2024-06-03T12:31+09:00")
                    ),
                    68382.05.toBigDecimal()
                ),
                StrategySignal(
                    OrderType.SELL,
                    Candle.TimeRange(
                        Duration.ofMinutes(1),
                        OffsetDateTime.parse("2024-06-03T22:46+09:00")
                    ),
                    70212.15.toBigDecimal()
                ),
                StrategySignal(
                    OrderType.BUY,
                    Candle.TimeRange(
                        Duration.ofMinutes(1),
                        OffsetDateTime.parse("2024-06-04T01:45+09:00")
                    ),
                    69254.34.toBigDecimal()
                )
            ),
            strategySignalHistory.strategySignals()
        )
    }
}