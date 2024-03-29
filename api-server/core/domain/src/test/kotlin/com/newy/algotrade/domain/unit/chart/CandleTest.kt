package com.newy.algotrade.domain.unit.chart

import com.newy.algotrade.domain.chart.Candle
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.Duration
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

class KotlinStudyTest {
    @Test
    fun `Duration 팩토리 메소드 캐싱 테스트`() {
        assertNotSame(Duration.ofMinutes(1), Duration.ofMinutes(1))
        assertNotSame(Duration.ofMinutes(1), 1.minutes.toJavaDuration())
    }

    @Test
    fun `BigDecimal 팩토리 메소드 캐싱 테스트`() {
        assertSame(BigDecimal.valueOf(0), BigDecimal.valueOf(0))
        assertSame(BigDecimal.valueOf(0), BigDecimal.ZERO)
        assertSame(BigDecimal.valueOf(0), 0.toBigDecimal())
    }
}

class CandleTest {
    @Test
    fun `Candle 기본 상태`() {
        val beginTime = ZonedDateTime.now()
        val candle = Candle.Factory.M1(
            beginTime = beginTime,
            openPrice = 1000.toBigDecimal(),
            highPrice = 2000.toBigDecimal(),
            lowPrice = 500.toBigDecimal(),
            closePrice = 1500.toBigDecimal(),
        )

        candle.time.run {
            assertEquals(beginTime, begin)
            assertEquals(beginTime.plusMinutes(1), end)
            assertEquals(1.minutes.toJavaDuration(), period)
        }
        assertEquals(1000.toBigDecimal(), candle.openPrice)
        assertEquals(2000.toBigDecimal(), candle.highPrice)
        assertEquals(500.toBigDecimal(), candle.lowPrice)
        assertEquals(1500.toBigDecimal(), candle.closePrice)
        assertEquals(0.toBigDecimal(), candle.volume)
    }

    @Test
    fun `Candle 팩토리 메소드`() {
        val beginTime = ZonedDateTime.now()

        assertEquals(1.minutes.toJavaDuration(), Candle.Factory.M1(beginTime).time.period)
        assertEquals(3.minutes.toJavaDuration(), Candle.Factory.M3(beginTime).time.period)
        assertEquals(5.minutes.toJavaDuration(), Candle.Factory.M5(beginTime).time.period)
        assertEquals(10.minutes.toJavaDuration(), Candle.Factory.M10(beginTime).time.period)
        assertEquals(1.hours.toJavaDuration(), Candle.Factory.H1(beginTime).time.period)
        assertEquals(1.days.toJavaDuration(), Candle.Factory.D1(beginTime).time.period)
    }

    @Test
    fun `Candle 동등성 테스트`() {
        val beginTime = ZonedDateTime.now()

        assertEquals(Candle.Factory.M1(beginTime), Candle.Factory.M1(beginTime))
        assertNotEquals(Candle.Factory.M1(beginTime), Candle.Factory.M1(beginTime.plusMinutes(1)))
        assertNotEquals(Candle.Factory.M1(beginTime), Candle.Factory.M3(beginTime))
    }

    @Test
    fun `highPrice 는 lowPrice 보다 작은 경우`() {
        assertThrows<IllegalArgumentException>("lowPrice <= closePrice") {
            Candle.Factory.M1(
                ZonedDateTime.now(),
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
            assertThrows<IllegalArgumentException>("lowPrice <= openPrice <= closePrice") {
                Candle.Factory.H1(
                    ZonedDateTime.now(),
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
            assertThrows<IllegalArgumentException>("lowPrice <= closePrice <= closePrice") {
                Candle.Factory.M1(
                    ZonedDateTime.now(),
                    lowPrice = 1000.toBigDecimal(),
                    closePrice = closePrice.toBigDecimal(),
                    highPrice = 2000.toBigDecimal(),
                    openPrice = 1500.toBigDecimal(),
                )
            }
        }
    }
}