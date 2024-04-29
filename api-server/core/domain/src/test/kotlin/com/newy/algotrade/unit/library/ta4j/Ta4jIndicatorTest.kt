package com.newy.algotrade.unit.library.ta4j

import com.newy.algotrade.domain.chart.*
import com.newy.algotrade.study.ta4j.TEST_TARGET_BAR_EXPECTED_VALUES
import com.newy.algotrade.study.ta4j.bybitKlineList
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneOffset

private fun getCandles(length: Int, list: Array<Array<String>> = bybitKlineList) =
    ChartFactory.TA4J.createCandles().also { results ->
        list.sliceArray(IntRange(0, length - 1))
            .also { it.reverse() }
            .forEach {
                val beginTimeMillis = it[0].toLong()
                val beginTime = Instant.ofEpochMilli(beginTimeMillis).atZone(ZoneOffset.UTC)
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
        val indicator = ChartFactory.TA4J.createADXIndicator(candles, 14)

        assertBigDecimalEquals(TEST_TARGET_BAR_EXPECTED_VALUES.getValue("ADX14"), indicator[candles.lastIndex])
    }

    @Test
    fun `RSI7 지수`() {
        val indicator = ChartFactory.TA4J.createRSIIndicator(candles, 7)

        assertBigDecimalEquals(TEST_TARGET_BAR_EXPECTED_VALUES.getValue("RSI7"), indicator[candles.lastIndex])
    }

    @Test
    fun `RSI14 지수`() {
        val indicator = ChartFactory.TA4J.createRSIIndicator(candles, 14)

        assertBigDecimalEquals(TEST_TARGET_BAR_EXPECTED_VALUES.getValue("RSI14"), indicator[candles.lastIndex])
    }

    @Test
    fun `RSI21 지수`() {
        val indicator = ChartFactory.TA4J.createRSIIndicator(candles, 21)

        assertBigDecimalEquals(TEST_TARGET_BAR_EXPECTED_VALUES.getValue("RSI21"), indicator[candles.lastIndex])
    }

    @Test
    fun `EMA50 지수`() {
        val indicator = ChartFactory.TA4J.createEMAIndicator(candles, 50)

        assertBigDecimalEquals(TEST_TARGET_BAR_EXPECTED_VALUES.getValue("EMA50"), indicator[candles.lastIndex])
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
