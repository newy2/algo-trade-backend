package com.newy.algotrade.unit.libs.ta4j

import com.newy.algotrade.domain.chart.*
import com.newy.algotrade.study.libs.ta4j.TEST_TARGET_BAR_EXPECTED_VALUES
import com.newy.algotrade.study.libs.ta4j.bybitKlineList
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

private fun getCandles(length: Int, list: Array<Array<String>> = bybitKlineList) =
    DEFAULT_CHART_FACTORY.candles().also { results ->
        list.sliceArray(IntRange(0, length - 1))
            .also { it.reverse() }
            .forEach {
                val beginTimeMillis = it[0].toLong()
                val beginTime = Instant.ofEpochMilli(beginTimeMillis).atOffset(ZoneOffset.UTC)
                results.upsert(
                    Candle.TimeFrame.M1(
                        beginTime,
                        it[1].toBigDecimal(),
                        it[2].toBigDecimal(),
                        it[3].toBigDecimal(),
                        it[4].toBigDecimal(),
                        it[5].toBigDecimal(),
                    )
                )
            }
    }

private fun assertBigDecimalEquals(expected: Double, actual: BigDecimal) {
    return assertEquals(expected, actual.toDouble(), 0.005)
}

class IndicatorTest {
    private lateinit var candles: Candles

    @BeforeEach
    fun setUp() {
        candles = getCandles(300)
    }

    @Test
    fun `ADX14 지표`() {
        val indicator = DEFAULT_CHART_FACTORY.adxIndicator(candles, 14)

        assertBigDecimalEquals(TEST_TARGET_BAR_EXPECTED_VALUES.getValue("ADX14"), indicator[candles.lastIndex])
    }

    @Test
    fun `RSI7 지수`() {
        val indicator = DEFAULT_CHART_FACTORY.rsiIndicator(candles, 7)

        assertBigDecimalEquals(TEST_TARGET_BAR_EXPECTED_VALUES.getValue("RSI7"), indicator[candles.lastIndex])
    }

    @Test
    fun `RSI14 지수`() {
        val indicator = DEFAULT_CHART_FACTORY.rsiIndicator(candles, 14)

        assertBigDecimalEquals(TEST_TARGET_BAR_EXPECTED_VALUES.getValue("RSI14"), indicator[candles.lastIndex])
    }

    @Test
    fun `RSI21 지수`() {
        val indicator = DEFAULT_CHART_FACTORY.rsiIndicator(candles, 21)

        assertBigDecimalEquals(TEST_TARGET_BAR_EXPECTED_VALUES.getValue("RSI21"), indicator[candles.lastIndex])
    }

    @Test
    fun `EMA50 지수`() {
        val indicator = DEFAULT_CHART_FACTORY.emaIndicator(candles, 50)

        assertBigDecimalEquals(TEST_TARGET_BAR_EXPECTED_VALUES.getValue("EMA50"), indicator[candles.lastIndex])
    }
}

class EtcIndicatorTest {
    @Test
    fun `상수 값 지수`() {
        val indicator = DEFAULT_CHART_FACTORY.constDecimalIndicator(20.0)
        repeat(10) {
            assertBigDecimalEquals(20.0, indicator[it])
        }
    }
}

class OpenClosePriceIndicatorTest {
    private val candles = DEFAULT_CHART_FACTORY.candles().also {
        it.upsert(
            Candle.TimeFrame.M1(
                OffsetDateTime.now(),
                openPrice = 100.0.toBigDecimal(),
                highPrice = 1000.0.toBigDecimal(),
                lowPrice = 100.0.toBigDecimal(),
                closePrice = 500.0.toBigDecimal(),
                volume = 0.0.toBigDecimal(),
            )
        )
    }

    @Test
    fun `시가 지수`() {
        val indicator = DEFAULT_CHART_FACTORY.openPriceIndicator(candles)

        assertBigDecimalEquals(100.0, indicator[0])
    }

    @Test
    fun `종가 지수`() {
        val indicator = DEFAULT_CHART_FACTORY.closePriceIndicator(candles)

        assertBigDecimalEquals(500.0, indicator[0])
    }
}


@DisplayName("테스트 헬퍼 메소드 테스트")
class HelperFunctionTest {
    private val bybitKlineList = arrayOf(
        arrayOf("1709942400000", "68165.0", "68224.8", "68165.0", "68185.8", "23.258"),
        arrayOf("1709942340000", "68196.1", "68196.1", "68151.5", "68165.0", "12.019"),
        arrayOf("1709942280000", "68247.7", "68247.8", "68167.3", "68196.1", "13.425"),
        arrayOf("1709942220000", "68256.1", "68257.3", "68239.0", "68247.7", "10.11"),
        arrayOf("1709942160000", "68231.5", "68257.3", "68231.5", "68256.1", "9.87"),
    )

    @Test
    fun `바이빗 클레인 리스트 데이터로 BarSeries 생성하기`() {
        val candles = getCandles(2, bybitKlineList)

        kotlin.test.assertEquals(2, candles.size)
        candles[candles.lastIndex].time.begin.run {
            kotlin.test.assertEquals(1709942400000, toInstant().toEpochMilli(), "bybitKlineList[0][0].toLong() 과 같음")
            kotlin.test.assertEquals("2024-03-09T00:00Z", toString())
        }
    }

    @Test
    fun `assertDoubleEquals 헬퍼 메소드`() {
        assertBigDecimalEquals(1.22, 1.224.toBigDecimal())
        assertBigDecimalEquals(1.23, 1.225.toBigDecimal())
    }
}
