package com.newy.algotrade.unit.back_testing.domain

import com.newy.algotrade.domain.back_testing.StringReporter
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.order.OrderType
import com.newy.algotrade.domain.chart.strategy.StrategySignal
import com.newy.algotrade.domain.chart.strategy.StrategySignalHistory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime

@DisplayName("빈 문자열 리포트")
class EmptyHistoryStringReporterTest {
    @Test
    fun `빈 히스토리 리포트`() {
        val emptyHistory = StrategySignalHistory()

        assertEquals("", StringReporter(emptyHistory).report())
    }
}

@DisplayName("롱주문 문자열 리포트")
class LongOrderStringReporterTest {
    private lateinit var history: StrategySignalHistory
    private lateinit var reporter: StringReporter

    @BeforeEach
    fun setUp() {
        history = StrategySignalHistory().also {
            val entrySignal = StrategySignal(
                orderType = OrderType.BUY,
                timeFrame = Candle.TimeRange(
                    Duration.ofMinutes(1),
                    OffsetDateTime.parse("2024-05-09T00:00+09:00")
                ),
                price = 1000.toBigDecimal()
            )
            it.add(entrySignal)
        }
        reporter = StringReporter(history)
    }

    @Test
    fun `entry 주문만 있는 경우`() {
        assertEquals(
            """
                ENTRY TIME: 2024-05-09T00:00+09:00
                EXIT TIME: -
                ENTRY PRICE: 1000
                EXIT PRICE: -
                --------------------
                TOTAL REVENUE: -
                TOTAL REVENUE RATE: -
                TOTAL TRANSACTION COUNT: 0 (ENTRY: 1, EXIT: 0)
            """.trimIndent(),
            reporter.report()
        )
    }

    @Test
    fun `entry, exit 주문이 있는 경우`() {
        StrategySignal(
            orderType = history.lastOrderType().completedType(),
            timeFrame = Candle.TimeRange(
                Duration.ofMinutes(1),
                OffsetDateTime.parse("2024-05-09T00:01+09:00")
            ),
            price = 1500.toBigDecimal()
        ).let { exitSignal ->
            history.add(exitSignal)
        }

        assertEquals(
            """
                ENTRY TIME: 2024-05-09T00:00+09:00
                EXIT TIME: 2024-05-09T00:01+09:00
                ENTRY PRICE: 1000
                EXIT PRICE: 1500
                --------------------
                TOTAL REVENUE: 500
                TOTAL REVENUE RATE: 50.00%
                TOTAL TRANSACTION COUNT: 1 (ENTRY: 1, EXIT: 1)
            """.trimIndent(),
            reporter.report()
        )
    }

    @Test
    fun `entry, exit, entry 주문이 있는 경우`() {
        StrategySignal(
            orderType = history.lastOrderType().completedType(),
            timeFrame = Candle.TimeRange(
                Duration.ofMinutes(1),
                OffsetDateTime.parse("2024-05-09T00:01+09:00")
            ),
            price = 1500.toBigDecimal()
        ).let { exitSignal ->
            history.add(exitSignal)
        }

        StrategySignal(
            orderType = history.lastOrderType().completedType(),
            timeFrame = Candle.TimeRange(
                Duration.ofMinutes(1),
                OffsetDateTime.parse("2024-05-09T00:02+09:00")
            ),
            price = 2000.toBigDecimal()
        ).let { nextEntrySignal ->
            history.add(nextEntrySignal)
        }

        assertEquals(
            """
                ENTRY TIME: 2024-05-09T00:00+09:00
                EXIT TIME: 2024-05-09T00:01+09:00
                ENTRY PRICE: 1000
                EXIT PRICE: 1500
                --------------------
                ENTRY TIME: 2024-05-09T00:02+09:00
                EXIT TIME: -
                ENTRY PRICE: 2000
                EXIT PRICE: -
                --------------------
                TOTAL REVENUE: 500
                TOTAL REVENUE RATE: 50.00%
                TOTAL TRANSACTION COUNT: 1 (ENTRY: 2, EXIT: 1)
            """.trimIndent(),
            reporter.report()
        )
    }
}

@DisplayName("숏주문 문자열 리포트")
class ShortOrderStringReporterTest {
    private lateinit var history: StrategySignalHistory
    private lateinit var reporter: StringReporter

    @BeforeEach
    fun setUp() {
        history = StrategySignalHistory().also {
            val entrySignal = StrategySignal(
                orderType = OrderType.SELL,
                timeFrame = Candle.TimeRange(
                    Duration.ofMinutes(1),
                    OffsetDateTime.parse("2024-05-09T00:00+09:00")
                ),
                price = 1000.toBigDecimal()
            )
            it.add(entrySignal)
        }
        reporter = StringReporter(history)
    }

    @Test
    fun `entry 주문만 있는 경우`() {
        assertEquals(
            """
                ENTRY TIME: 2024-05-09T00:00+09:00
                EXIT TIME: -
                ENTRY PRICE: 1000
                EXIT PRICE: -
                --------------------
                TOTAL REVENUE: -
                TOTAL REVENUE RATE: -
                TOTAL TRANSACTION COUNT: 0 (ENTRY: 1, EXIT: 0)
            """.trimIndent(),
            reporter.report()
        )
    }

    @Test
    fun `entry, exit 주문이 있는 경우`() {
        StrategySignal(
            orderType = history.lastOrderType().completedType(),
            timeFrame = Candle.TimeRange(
                Duration.ofMinutes(1),
                OffsetDateTime.parse("2024-05-09T00:01+09:00")
            ),
            price = 500.toBigDecimal()
        ).let { exitSignal ->
            history.add(exitSignal)
        }

        assertEquals(
            """
                ENTRY TIME: 2024-05-09T00:00+09:00
                EXIT TIME: 2024-05-09T00:01+09:00
                ENTRY PRICE: 1000
                EXIT PRICE: 500
                --------------------
                TOTAL REVENUE: 500
                TOTAL REVENUE RATE: 50.00%
                TOTAL TRANSACTION COUNT: 1 (ENTRY: 1, EXIT: 1)
            """.trimIndent(),
            reporter.report()
        )
    }

    @Test
    fun `entry, exit, entry 주문이 있는 경우`() {
        StrategySignal(
            orderType = history.lastOrderType().completedType(),
            timeFrame = Candle.TimeRange(
                Duration.ofMinutes(1),
                OffsetDateTime.parse("2024-05-09T00:01+09:00")
            ),
            price = 500.toBigDecimal()
        ).let { exitSignal ->
            history.add(exitSignal)
        }

        StrategySignal(
            orderType = history.lastOrderType().completedType(),
            timeFrame = Candle.TimeRange(
                Duration.ofMinutes(1),
                OffsetDateTime.parse("2024-05-09T00:02+09:00")
            ),
            price = 300.toBigDecimal()
        ).let { nextEntrySignal ->
            history.add(nextEntrySignal)
        }

        assertEquals(
            """
                ENTRY TIME: 2024-05-09T00:00+09:00
                EXIT TIME: 2024-05-09T00:01+09:00
                ENTRY PRICE: 1000
                EXIT PRICE: 500
                --------------------
                ENTRY TIME: 2024-05-09T00:02+09:00
                EXIT TIME: -
                ENTRY PRICE: 300
                EXIT PRICE: -
                --------------------
                TOTAL REVENUE: 500
                TOTAL REVENUE RATE: 50.00%
                TOTAL TRANSACTION COUNT: 1 (ENTRY: 2, EXIT: 1)
            """.trimIndent(),
            reporter.report()
        )
    }
}