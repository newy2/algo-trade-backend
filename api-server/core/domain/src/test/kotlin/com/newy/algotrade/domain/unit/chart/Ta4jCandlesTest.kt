package com.newy.algotrade.domain.unit.chart

import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.Ta4jCandles
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.ZonedDateTime
import kotlin.test.assertEquals

private fun oneMinuteCandle(beginTime: ZonedDateTime, price: Number) =
    Candle.Factory.M1(
        beginTime,
        openPrice = price.toDouble().toBigDecimal(),
        highPrice = price.toDouble().toBigDecimal(),
        lowPrice = price.toDouble().toBigDecimal(),
        closePrice = price.toDouble().toBigDecimal(),
        volume = BigDecimal.ZERO,
    )

private fun oneHourCandle(beginTime: ZonedDateTime, price: Number) =
    Candle.Factory.H1(
        beginTime,
        openPrice = price.toDouble().toBigDecimal(),
        highPrice = price.toDouble().toBigDecimal(),
        lowPrice = price.toDouble().toBigDecimal(),
        closePrice = price.toDouble().toBigDecimal(),
        volume = BigDecimal.ZERO,
    )

@DisplayName("기본 생성자 상태 확인")
class EmptyTa4jCandlesTest {
    @Test
    fun `기본 생성자`() {
        val candles = Ta4jCandles()

        assertEquals(0, candles.size)
        assertEquals(400, candles.maxSize)
        assertEquals(-1, candles.firstIndex)
        assertEquals(-1, candles.lastIndex)
    }
}

@DisplayName("기본 기능 테스트")
class Ta4jCandlesTest {
    private val beginTime = ZonedDateTime.parse("2024-03-09T00:00:00Z")
    private lateinit var candles: Ta4jCandles

    @BeforeEach
    fun setUp() {
        candles = Ta4jCandles()
        candles.upsert(oneMinuteCandle(beginTime, 1000))
    }

    @Test
    fun `캔들 등록하기`() {
        candles.upsert(oneMinuteCandle(beginTime.plusMinutes(1), 2000))

        assertEquals(2, candles.size)
        assertEquals(oneMinuteCandle(beginTime, 1000), candles[candles.firstIndex])
        assertEquals(oneMinuteCandle(beginTime.plusMinutes(1), 2000), candles[candles.lastIndex])
    }

    @Test
    fun `마지막 캔들 replace 하기`() {
        val sameBeginTime = beginTime
        candles.upsert(oneMinuteCandle(sameBeginTime, 2000))

        assertEquals(1, candles.size)
        assertEquals(oneMinuteCandle(beginTime, 2000), candles[candles.firstIndex])
    }

    @Test
    fun `과거 시간 캔들을 등록하는 경우`() {
        assertThrows<IllegalArgumentException>("과거 시간의 Candle 을 등록하면 에러발생") {
            candles.upsert(oneMinuteCandle(beginTime.minusMinutes(1), 2000))
        }
    }

    @Test
    fun `시간 간격이 다른 Candle 을 등록하는 경우`() {
        assertThrows<IllegalArgumentException>("1분봉 Candles 에 1시간봉 Candle 을 등록하면 에러발생") {
            candles.upsert(oneHourCandle(beginTime.plusMinutes(1), 2000))
        }
    }

    @Test
    fun `마지막 캔들 beginTime 과 endTime 사이의 시간으로 Candle 을 등록하는 경우`() {
        assertThrows<IllegalArgumentException>("마지막 캔들 beginTime 이 00:00:00 이고, 신규 캔들 beginTime 이 00:00:30 이면 에러발생") {
            candles.upsert(oneMinuteCandle(beginTime.plusSeconds(30), 2000))
        }
    }

    @Test
    fun `index 범위를 벗어난 경우`() {
        assertThrows<IndexOutOfBoundsException> {
            candles[candles.firstIndex - 1]
        }
        assertThrows<IndexOutOfBoundsException> {
            candles[candles.lastIndex - 1]
        }
    }

    // TODO Candles 의 이름을 알아야 하나?
}

@DisplayName("최대 크기 제한")
class Ta4jCandlesMaxSizeTest {
    @Test
    fun `BarSeries 에 등록된 Bar 는 FIFO 방식으로 제거된다`() {
        val beginTime = ZonedDateTime.parse("2024-03-09T00:00:00Z")
        val candles = Ta4jCandles(maxSize = 3).also {
            it.upsert(oneMinuteCandle(beginTime.plusMinutes(0), 1000))
            it.upsert(oneMinuteCandle(beginTime.plusMinutes(1), 2000))
            it.upsert(oneMinuteCandle(beginTime.plusMinutes(2), 3000))
            it.upsert(oneMinuteCandle(beginTime.plusMinutes(3), 4000))
        }

        assertEquals(3, candles.maxSize)
        assertEquals(oneMinuteCandle(beginTime.plusMinutes(1), 2000), candles[candles.firstIndex])
        assertEquals(oneMinuteCandle(beginTime.plusMinutes(3), 4000), candles[candles.lastIndex])
    }

}