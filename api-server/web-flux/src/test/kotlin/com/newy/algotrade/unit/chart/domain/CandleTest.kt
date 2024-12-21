package com.newy.algotrade.unit.chart.domain

import com.newy.algotrade.chart.domain.Candle
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

class CandleTest {
    @Test
    fun `Candle 기본 상태`() {
        val beginTime = OffsetDateTime.now()
        val candle = Candle.TimeFrame.M1(
            beginTime = beginTime,
            openPrice = 1000.toBigDecimal(),
            highPrice = 2000.toBigDecimal(),
            lowPrice = 500.toBigDecimal(),
            closePrice = 1500.toBigDecimal(),
        )

        candle.time.let {
            assertEquals(beginTime, it.begin)
            assertEquals(beginTime.plusMinutes(1), it.end)
            assertEquals(1.minutes.toJavaDuration(), it.period)
        }
        candle.price.let {
            assertEquals(1000.toBigDecimal(), it.open)
            assertEquals(2000.toBigDecimal(), it.high)
            assertEquals(500.toBigDecimal(), it.low)
            assertEquals(1500.toBigDecimal(), it.close)
        }
        assertEquals(0.toBigDecimal(), candle.volume)
    }

    @Test
    fun `Candle 팩토리 메소드`() {
        val beginTime = OffsetDateTime.now()

        assertEquals(1.minutes.toJavaDuration(), Candle.TimeFrame.M1(beginTime).time.period)
        assertEquals(3.minutes.toJavaDuration(), Candle.TimeFrame.M3(beginTime).time.period)
        assertEquals(5.minutes.toJavaDuration(), Candle.TimeFrame.M5(beginTime).time.period)
        assertEquals(15.minutes.toJavaDuration(), Candle.TimeFrame.M15(beginTime).time.period)
        assertEquals(30.minutes.toJavaDuration(), Candle.TimeFrame.M30(beginTime).time.period)
        assertEquals(1.hours.toJavaDuration(), Candle.TimeFrame.H1(beginTime).time.period)
        assertEquals(1.days.toJavaDuration(), Candle.TimeFrame.D1(beginTime).time.period)
    }

    @Test
    fun `Candle 동등성 테스트`() {
        val beginTime = OffsetDateTime.now()

        assertEquals(Candle.TimeFrame.M1(beginTime), Candle.TimeFrame.M1(beginTime))
        assertNotEquals(Candle.TimeFrame.M1(beginTime), Candle.TimeFrame.M1(beginTime.plusMinutes(1)))
        assertNotEquals(Candle.TimeFrame.M1(beginTime), Candle.TimeFrame.M3(beginTime))
    }

    @Test
    fun `highPrice 가 lowPrice 보다 작은 경우`() {
        assertThrows<IllegalArgumentException>("lowPrice <= highPrice 이어야 한다") {
            Candle.TimeFrame.M1(
                OffsetDateTime.now(),
                highPrice = 1000.toBigDecimal(),
                lowPrice = 2000.toBigDecimal(),
                openPrice = 1500.toBigDecimal(),
                closePrice = 1500.toBigDecimal(),
            )
        }
    }

    @Test
    fun `openPrice 는 highPrice 와 lowPrice 사이에 있어야 한다`() {
        arrayOf(100, 3000).forEach { openPrice ->
            assertThrows<IllegalArgumentException>("lowPrice <= openPrice <= highPrice 이어야 한다") {
                Candle.TimeFrame.H1(
                    OffsetDateTime.now(),
                    lowPrice = 1000.toBigDecimal(),
                    openPrice = openPrice.toBigDecimal(),
                    highPrice = 2000.toBigDecimal(),
                    closePrice = 1500.toBigDecimal(),
                )
            }
        }
    }

    @Test
    fun `closePrice 는 highPrice 와 lowPRice 사이에 있어야 한다`() {
        arrayOf(100, 3000).forEach { closePrice ->
            assertThrows<IllegalArgumentException>("lowPrice <= closePrice <= highPrice 이어야 한다") {
                Candle.TimeFrame.M1(
                    OffsetDateTime.now(),
                    lowPrice = 1000.toBigDecimal(),
                    closePrice = closePrice.toBigDecimal(),
                    highPrice = 2000.toBigDecimal(),
                    openPrice = 1500.toBigDecimal(),
                )
            }
        }
    }

    @Test
    fun `가격 정보가 같은 경우`() {
        assertDoesNotThrow("에러가 발생하지 않아야 한다") {
            Candle.TimeFrame.M1(
                OffsetDateTime.now(),
                openPrice = 1000.toBigDecimal(),
                highPrice = 1000.toBigDecimal(),
                lowPrice = 1000.toBigDecimal(),
                closePrice = 1000.toBigDecimal(),
            )
        }
    }

    @Test
    fun `팩토리 메소드 오버로드`() {
        val epochMilli: Long = Instant.now().toEpochMilli()
        val dateTime: OffsetDateTime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneOffset.UTC)

        assertEquals(
            Candle.TimeFrame.M1(
                epochMilli,
                openPrice = 1000.toBigDecimal(),
                highPrice = 1000.toBigDecimal(),
                lowPrice = 1000.toBigDecimal(),
                closePrice = 1000.toBigDecimal(),
            ),
            Candle.TimeFrame.M1(
                dateTime,
                openPrice = 1000.toBigDecimal(),
                highPrice = 1000.toBigDecimal(),
                lowPrice = 1000.toBigDecimal(),
                closePrice = 1000.toBigDecimal(),
            )
        )

    }
}