package com.newy.algotrade.unit.price2.domain.back_test

import com.newy.algotrade.coroutine_based_application.price2.domain.back_test.StringReporter
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.order.OrderSignal
import com.newy.algotrade.domain.chart.order.OrderSignalHistory
import com.newy.algotrade.domain.chart.order.OrderType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime

@DisplayName("롱주문 문자열 리포트")
class LongOrderStringReporterTest {
    private val emptyHistory = OrderSignalHistory()
    private lateinit var history: OrderSignalHistory

    @BeforeEach
    fun setUp() {
        history = OrderSignalHistory().also {
            it.add(
                OrderSignal(
                    OrderType.BUY,
                    Candle.TimeRange(
                        Duration.ofMinutes(1),
                        OffsetDateTime.parse("2024-05-09T00:00+09:00")
                    ),
                    1000.toBigDecimal()
                )
            )
        }
    }

    @Test
    fun empty() {
        val reporter = StringReporter(emptyHistory)
        assertEquals("", reporter.report())
    }

    @Test
    fun `entry 주문만 있는 경우`() {
        val reporter = StringReporter(history)
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
        history.add(
            OrderSignal(
                history.lastOrderType().completedType(),
                Candle.TimeRange(
                    Duration.ofMinutes(1),
                    OffsetDateTime.parse("2024-05-09T00:01+09:00")
                ),
                1500.toBigDecimal()
            )
        )

        val reporter = StringReporter(history)
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
        history.add(
            OrderSignal(
                history.lastOrderType().completedType(),
                Candle.TimeRange(
                    Duration.ofMinutes(1),
                    OffsetDateTime.parse("2024-05-09T00:01+09:00")
                ),
                1500.toBigDecimal()
            )
        )
        history.add(
            OrderSignal(
                history.lastOrderType().completedType(),
                Candle.TimeRange(
                    Duration.ofMinutes(1),
                    OffsetDateTime.parse("2024-05-09T00:02+09:00")
                ),
                2000.toBigDecimal()
            )
        )

        val reporter = StringReporter(history)
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
    private lateinit var history: OrderSignalHistory

    @BeforeEach
    fun setUp() {
        history = OrderSignalHistory().also {
            it.add(
                OrderSignal(
                    OrderType.SELL,
                    Candle.TimeRange(
                        Duration.ofMinutes(1),
                        OffsetDateTime.parse("2024-05-09T00:00+09:00")
                    ),
                    1000.toBigDecimal()
                )
            )
        }
    }

    @Test
    fun `entry 주문만 있는 경우`() {
        val reporter = StringReporter(history)
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
        history.add(
            OrderSignal(
                history.lastOrderType().completedType(),
                Candle.TimeRange(
                    Duration.ofMinutes(1),
                    OffsetDateTime.parse("2024-05-09T00:01+09:00")
                ),
                500.toBigDecimal()
            )
        )

        val reporter = StringReporter(history)
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
        history.add(
            OrderSignal(
                history.lastOrderType().completedType(),
                Candle.TimeRange(
                    Duration.ofMinutes(1),
                    OffsetDateTime.parse("2024-05-09T00:01+09:00")
                ),
                500.toBigDecimal()
            )
        )
        history.add(
            OrderSignal(
                history.lastOrderType().completedType(),
                Candle.TimeRange(
                    Duration.ofMinutes(1),
                    OffsetDateTime.parse("2024-05-09T00:02+09:00")
                ),
                300.toBigDecimal()
            )
        )

        val reporter = StringReporter(history)
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