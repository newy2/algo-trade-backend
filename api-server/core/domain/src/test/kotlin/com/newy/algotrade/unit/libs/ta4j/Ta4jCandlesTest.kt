package com.newy.algotrade.unit.libs.ta4j

import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.chart.ChartFactory
import com.newy.algotrade.domain.chart.libs.ta4j.Ta4jCandles
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.ZonedDateTime
import kotlin.test.assertEquals

private fun oneMinuteCandle(beginTime: ZonedDateTime, price: Number) =
    Candle.TimeFrame.M1(
        beginTime,
        openPrice = price.toDouble().toBigDecimal(),
        highPrice = price.toDouble().toBigDecimal(),
        lowPrice = price.toDouble().toBigDecimal(),
        closePrice = price.toDouble().toBigDecimal(),
        volume = BigDecimal.ZERO,
    )

private fun oneHourCandle(beginTime: ZonedDateTime, price: Number) =
    Candle.TimeFrame.H1(
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
        val candles = ChartFactory.TA4J.createCandles()

        assertEquals(0, candles.size)
        assertEquals(400, candles.maxSize)
        assertEquals(-1, candles.firstIndex)
        assertEquals(-1, candles.lastIndex)
    }
}

@DisplayName("기본 기능 테스트")
class Ta4jCandlesTest {
    private lateinit var beginTime: ZonedDateTime
    private lateinit var candles: Candles

    @BeforeEach
    fun setUp() {
        beginTime = ZonedDateTime.parse("2024-03-09T00:00:00Z")
        candles = ChartFactory.TA4J.createCandles().also {
            it.upsert(oneMinuteCandle(beginTime, 1000))
        }
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
            val beforeBeginTime = beginTime.minusMinutes(1)
            candles.upsert(oneMinuteCandle(beforeBeginTime, 2000))
        }
    }

    @Test
    fun `마지막 캔들 beginTime 과 endTime 사이의 시간으로 Candle 을 등록하는 경우`() {
        assertThrows<IllegalArgumentException>("마지막 beginTime 이 00:00:00 이고, 신규 beginTime 이 00:00:30 이면 에러발생") {
            val irregularBeginTime = beginTime.plusSeconds(30)
            candles.upsert(oneMinuteCandle(irregularBeginTime, 2000))
        }
    }

    @Test
    fun `시간 간격이 다른 Candle 을 등록하는 경우`() {
        assertThrows<IllegalArgumentException>("1분봉 Candles 에 1시간봉 Candle 을 등록하면 에러발생") {
            val hour1Candle = oneHourCandle(beginTime.plusMinutes(1), 2000)
            candles.upsert(hour1Candle)
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
    // TODO Candles#upsert 연속적인 시간대의 Candle 이 확인하는 로직?
    //      TODO 시작시간 종료시간이 있는 금융시장: 예) 국내/해외 주식
    //      TODO 시작시간 종료시간이 없는 금융시장: 예) 암호화폐 거래
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