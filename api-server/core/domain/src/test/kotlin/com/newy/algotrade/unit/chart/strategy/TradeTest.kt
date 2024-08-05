package com.newy.algotrade.unit.chart.strategy

import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.order.OrderType
import com.newy.algotrade.domain.chart.strategy.StrategySignal
import com.newy.algotrade.domain.chart.strategy.Trade
import com.newy.algotrade.domain.chart.strategy.TradeResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime


@DisplayName("롱 포지션 거래 결과 테스트")
class LongPositionTradeResultTest {
    private val entrySignal = StrategySignal(
        OrderType.BUY,
        Candle.TimeRange(
            Duration.ofMinutes(1),
            OffsetDateTime.parse("2024-05-09T00:00+09:00")
        ),
        1000.toBigDecimal()
    )

    @Test
    fun `진입, 진출 가격이 같은 경우`() {
        val exitSignal = StrategySignal(
            OrderType.SELL,
            Candle.TimeRange(
                Duration.ofMinutes(1),
                OffsetDateTime.parse("2024-05-09T00:00+09:00")
            ),
            1000.0.toBigDecimal()
        )

        assertEquals(TradeResult.DRAW, Trade(entrySignal, exitSignal).result())
    }

    @Test
    fun `진출 가격이 높은 경우`() {
        val exitSignal = StrategySignal(
            OrderType.SELL,
            Candle.TimeRange(
                Duration.ofMinutes(1),
                OffsetDateTime.parse("2024-05-09T00:00+09:00")
            ),
            1100.toBigDecimal()
        )

        assertEquals(TradeResult.WIN, Trade(entrySignal, exitSignal).result())
    }

    @Test
    fun `진출 가격이 낮은 경우`() {
        val exitSignal = StrategySignal(
            OrderType.SELL,
            Candle.TimeRange(
                Duration.ofMinutes(1),
                OffsetDateTime.parse("2024-05-09T00:00+09:00")
            ),
            900.toBigDecimal()
        )

        assertEquals(TradeResult.LOSS, Trade(entrySignal, exitSignal).result())
    }
}

@DisplayName("숏 포지션 거래 결과 테스트")
class ShortPositionTradeResultTest {
    private val entrySignal = StrategySignal(
        OrderType.SELL,
        Candle.TimeRange(
            Duration.ofMinutes(1),
            OffsetDateTime.parse("2024-05-09T00:00+09:00")
        ),
        1000.toBigDecimal()
    )

    @Test
    fun `진입, 진출 가격이 같은 경우`() {
        val exitSignal = StrategySignal(
            OrderType.BUY,
            Candle.TimeRange(
                Duration.ofMinutes(1),
                OffsetDateTime.parse("2024-05-09T00:00+09:00")
            ),
            1000.0.toBigDecimal()
        )

        assertEquals(TradeResult.DRAW, Trade(entrySignal, exitSignal).result())
    }

    @Test
    fun `진출 가격이 높은 경우`() {
        val exitSignal = StrategySignal(
            OrderType.BUY,
            Candle.TimeRange(
                Duration.ofMinutes(1),
                OffsetDateTime.parse("2024-05-09T00:00+09:00")
            ),
            1100.toBigDecimal()
        )

        assertEquals(TradeResult.LOSS, Trade(entrySignal, exitSignal).result())
    }

    @Test
    fun `진출 가격이 낮은 경우`() {
        val exitSignal = StrategySignal(
            OrderType.BUY,
            Candle.TimeRange(
                Duration.ofMinutes(1),
                OffsetDateTime.parse("2024-05-09T00:00+09:00")
            ),
            900.toBigDecimal()
        )

        assertEquals(TradeResult.WIN, Trade(entrySignal, exitSignal).result())
    }
}