package com.newy.algotrade.study.libs.ta4j

import helpers.SimpleCsvParser
import org.junit.jupiter.api.*
import org.opentest4j.AssertionFailedError
import org.ta4j.core.BarSeries
import org.ta4j.core.BaseBar
import org.ta4j.core.BaseBarSeries
import org.ta4j.core.indicators.EMAIndicator
import org.ta4j.core.indicators.RSIIndicator
import org.ta4j.core.indicators.adx.ADXIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import org.ta4j.core.num.Num
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

val bybitKlineList =
    SimpleCsvParser.parseFromResource("/csv/[ByBit] BTCUSDT - 1m - 1000count - until 2024-03-09T00:00Z(UTC).csv")

val TEST_TARGET_BAR_EXPECTED_VALUES = mapOf(
    "RSI7" to 44.49,
    "RSI14" to 46.83,
    "RSI21" to 47.06,
    "ADX14" to 16.15,
    "EMA50" to 68232.69
)

private fun getBarSeries(length: Int, list: Array<Array<String>> = bybitKlineList) =
    BaseBarSeries().also { results ->
        list.sliceArray(IntRange(0, length - 1))
            .also { it.reverse() }
            .forEach {
                val endTimeMillis = it[0].toLong() + 1.minutes.toJavaDuration().toMillis()
                val endTime = Instant.ofEpochMilli(endTimeMillis).atZone(ZoneOffset.UTC)
                results.addBar(
                    BaseBar(
                        1.minutes.toJavaDuration(),
                        endTime,
                        it[1],
                        it[2],
                        it[3],
                        it[4],
                        it[5],
                    )
                )
            }
    }

fun assertDoubleNumEquals(expected: Double, actual: Num) =
    assertEquals(expected, actual.doubleValue(), 0.005)

@DisplayName("바이빗 BTC/USDT 클레인 데이터로 지수 검증하기")
class IndicatorTest {
    private lateinit var series: BarSeries

    @BeforeEach
    fun setUp() {
        series = getBarSeries(300)
    }

    @Test
    fun `테스트 대상 Bar 의 beginTime 확인하기`() {
        assertEquals("2024-03-09T00:00Z", series.lastBar.beginTime.toString())
    }

    @Test
    fun `ADX14 지수`() {
        val indicator = ADXIndicator(series, 14)

        assertDoubleNumEquals(TEST_TARGET_BAR_EXPECTED_VALUES.getValue("ADX14"), indicator.getValue(series.endIndex))
    }

    @Test
    fun `RSI7 지수`() {
        val basePrice = ClosePriceIndicator(series)
        val indicator = RSIIndicator(basePrice, 7)

        assertDoubleNumEquals(TEST_TARGET_BAR_EXPECTED_VALUES.getValue("RSI7"), indicator.getValue(series.endIndex))
    }

    @Test
    fun `RSI14 지수`() {
        val basePrice = ClosePriceIndicator(series)
        val indicator = RSIIndicator(basePrice, 14)

        assertDoubleNumEquals(TEST_TARGET_BAR_EXPECTED_VALUES.getValue("RSI14"), indicator.getValue(series.endIndex))
    }

    @Test
    fun `RSI21 지수`() {
        val basePrice = ClosePriceIndicator(series)
        val indicator = RSIIndicator(basePrice, 21)

        assertDoubleNumEquals(TEST_TARGET_BAR_EXPECTED_VALUES.getValue("RSI21"), indicator.getValue(series.endIndex))
    }

    @Test
    fun `EMA50 지수`() {
        val basePrice = ClosePriceIndicator(series)
        val indicator = EMAIndicator(basePrice, 50)

        assertDoubleNumEquals(TEST_TARGET_BAR_EXPECTED_VALUES.getValue("EMA50"), indicator.getValue(series.endIndex))
    }
}

@DisplayName("Indicator#getvalue 를 사용시, index 기준으로 BarSeries 의 과거 데이터가 적으면, 지수의 오차값이 발생한다")
class IndicatorExceptionTest {
    @Test
    fun `작은 BarSeries 로 EMA50 를 계산하는 경우 오차가 발생한다`() {
        // 참고: https://ta4j.github.io/ta4j-wiki/FAQ.html#why-does-my-indicator-not-match-someone-elses-values

        arrayOf(100, 200, 250).forEach { smallSize ->
            val smallSizeSeries = getBarSeries(smallSize)
            val basePrice = ClosePriceIndicator(smallSizeSeries)
            val indicator = EMAIndicator(basePrice, 50)

            assertThrows<AssertionFailedError> {
                assertDoubleNumEquals(
                    TEST_TARGET_BAR_EXPECTED_VALUES.getValue("EMA50"),
                    indicator.getValue(smallSizeSeries.endIndex)
                )
            }
        }
    }
}

@DisplayName("테스트 헬퍼 메소드 테스트")
class GetBarSeriesTest {
    private val bybitKlineList = arrayOf(
        arrayOf("1709942400000", "68165.0", "68224.8", "68165.0", "68185.8", "23.258"),
        arrayOf("1709942340000", "68196.1", "68196.1", "68151.5", "68165.0", "12.019"),
        arrayOf("1709942280000", "68247.7", "68247.8", "68167.3", "68196.1", "13.425"),
    )

    @Test
    fun `바이빗 클레인 리스트 데이터로 BarSeries 생성하기`() {
        val series = getBarSeries(2, bybitKlineList)

        assertEquals(2, series.barCount)
        series.lastBar.beginTime.run {
            assertEquals(1709942400000, toInstant().toEpochMilli(), "bybitKlineList[0][0].toLong() 과 같음")
            assertEquals("2024-03-09T00:00Z", toString())
        }
    }
}
