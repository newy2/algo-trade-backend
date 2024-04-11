package com.newy.algotrade.domain.study.library.ta4j

import org.junit.jupiter.api.*
import org.ta4j.core.BarSeries
import org.ta4j.core.BaseBar
import org.ta4j.core.BaseBarSeries
import org.ta4j.core.num.DecimalNum
import org.ta4j.core.num.DoubleNum
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

fun decimalNumBar(endTime: ZonedDateTime, price: Number = 1000) = BaseBar(
    1.minutes.toJavaDuration(),
    endTime,
    price.toDouble().toBigDecimal(),
    price.toDouble().toBigDecimal(),
    price.toDouble().toBigDecimal(),
    price.toDouble().toBigDecimal(),
    0.toBigDecimal(),
)

fun doubleNumBar(endTime: ZonedDateTime, price: Number = 1000) = BaseBar(
    1.minutes.toJavaDuration(),
    endTime,
    price.toDouble(),
    price.toDouble(),
    price.toDouble(),
    price.toDouble(),
    0.toDouble(),
)

@DisplayName("BaseBarSeries 객체의 Num 타입 테스트")
class BaseBarSeriesNumTypeTest {
    private lateinit var defaultBarSeries: BarSeries
    private lateinit var decimalNumBarSeries: BarSeries
    private lateinit var doubleNumBarSeries: BarSeries

    @BeforeEach
    fun setUp() {
        defaultBarSeries = BaseBarSeries()
        decimalNumBarSeries = BaseBarSeries("name of series", DecimalNum::valueOf)
        doubleNumBarSeries = BaseBarSeries("name of series", DoubleNum::valueOf)
    }

    @Test
    fun `DecimalNum 기반의 BaseBarSeries 객체`() {
        assertTrue(defaultBarSeries.numOf(1) is DecimalNum)
        assertTrue(decimalNumBarSeries.numOf(1) is DecimalNum)
    }

    @Test
    fun `DoubleNum 기반의 BaseBarSeries 객체`() {
        assertTrue(doubleNumBarSeries.numOf(1) is DoubleNum)
    }

    @Test
    fun `다른 Num 타입의 Bar 를 추가하는 경우 에러 발생한다`() {
        assertThrows<IllegalArgumentException>("Decimal 시리즈에 Double 바를 추가한 경우") {
            val otherTypeNumBar = doubleNumBar(ZonedDateTime.now())
            decimalNumBarSeries.addBar(otherTypeNumBar)
        }
        assertThrows<IllegalArgumentException>("Double 시리즈에 Decimal 바를 추가한 경우") {
            val otherTypeNumBar = decimalNumBar(ZonedDateTime.now())
            doubleNumBarSeries.addBar(otherTypeNumBar)
        }
    }
}


@DisplayName("BaseBar 에 Bar 추가하는 방법")
class BaseBarSeriesAddBarTest {
    private lateinit var series: BarSeries
    private lateinit var endTime: ZonedDateTime

    @BeforeEach
    fun setUp() {
        endTime = ZonedDateTime.now()
        series = BaseBarSeries().also {
            it.addBar(decimalNumBar(endTime, 1000))
        }
    }

    @Test
    fun `Bar 추가하기`() {
        val nextEndTime = endTime.plusMinutes(1)

        series.addBar(decimalNumBar(nextEndTime, 2000))

        assertEquals(2, series.barCount)
        series.run {
            // 첫 번째 Bar 를 가져오는 방법
            assertEquals(decimalNumBar(endTime, 1000), getBar(beginIndex))
            assertEquals(decimalNumBar(endTime, 1000), firstBar)
        }
        series.run {
            // 마지막 Bar 를 가져오는 방법
            assertEquals(decimalNumBar(nextEndTime, 2000), getBar(endIndex))
            assertEquals(decimalNumBar(nextEndTime, 2000), lastBar)
        }
    }

    @Test
    fun `새로 추가하는 Bar 의 endTime 은 마지막 endTime 보다 커야한다`() {
        assertThrows<IllegalArgumentException>("마지막 endTime 과 같은 경우") {
            val sameEndTime = endTime
            series.addBar(decimalNumBar(sameEndTime))
        }
        assertThrows<IllegalArgumentException>("마지막 endTime 보다 과거인 경우") {
            val beforeEndTime = endTime.minusMinutes(1)
            series.addBar(decimalNumBar(beforeEndTime))
        }
    }

    @Test
    fun `BarSeries 의 마지막 Bar 를 교체하는 방법`() {
        val sameEndTime = endTime
        val isReplace = true

        series.addBar(decimalNumBar(sameEndTime, 2000), isReplace)

        assertEquals(1, series.barCount)
        assertEquals(decimalNumBar(endTime, 2000), series.lastBar)
    }
}

@DisplayName("등록 가능한 Bar 갯수를 제한하는 방법")
class BaseBarMaximumBarCountTest {
    private lateinit var series: BarSeries
    private lateinit var endTime: ZonedDateTime

    @BeforeEach
    fun setUp() {
        series = BaseBarSeries()
        endTime = ZonedDateTime.now()

        series.maximumBarCount = 3

        series.addBar(decimalNumBar(endTime.plusMinutes(0), 1000))
        series.addBar(decimalNumBar(endTime.plusMinutes(1), 2000))
        series.addBar(decimalNumBar(endTime.plusMinutes(2), 3000))
        series.addBar(decimalNumBar(endTime.plusMinutes(3), 4000))
        series.addBar(decimalNumBar(endTime.plusMinutes(4), 5000))
        series.addBar(decimalNumBar(endTime.plusMinutes(5), 6000))
    }

    @Test
    fun `BarSeries 상태 값 설명`() {
        series.run {
            assertEquals(3, barCount, "등록된 Bar 갯수")
            assertEquals(3, removedBarsCount, "삭제된 Bar 갯수")
            assertEquals(0, beginIndex, "첫 번째 Bar index")
            assertEquals(5, endIndex, "마지막 Bar index")
            assertEquals(endIndex, (barCount + removedBarsCount) - 1, "endIndex 계산식")
        }
    }

    @Test
    fun `BarSeries 에 등록된 Bar 는 FIFO 방식으로 제거된다`() {
        series.run {
            assertEquals(decimalNumBar(endTime.plusMinutes(3), 4000), getBar(beginIndex))
            assertEquals(decimalNumBar(endTime.plusMinutes(5), 6000), getBar(endIndex))
        }
    }

    @Test
    fun `BarSeries#getBar 함수를 사용시, index 범위를 벗어난 경우`() {
        assertThrows<IndexOutOfBoundsException> {
            series.getBar(series.beginIndex - 1)
        }
        assertThrows<IndexOutOfBoundsException> {
            series.getBar(series.endIndex + 1)
        }
    }

    @Test
    fun `BarSeries#getBar 함수 사용시, index 에 정확히 매칭되지 않는 경우, 첫번 째 Bar 를 리턴한다`() {
        series.run {
            assertEquals(decimalNumBar(endTime.plusMinutes(3), 4000), getBar(endIndex - 5))
            assertEquals(decimalNumBar(endTime.plusMinutes(3), 4000), getBar(endIndex - 4), "특이 케이스: firstBar 리턴")
            assertEquals(decimalNumBar(endTime.plusMinutes(3), 4000), getBar(endIndex - 3), "특이 케이스: firstBar 리턴")
            assertEquals(decimalNumBar(endTime.plusMinutes(3), 4000), getBar(endIndex - 2), "특이 케이스: firstBar 리턴")
            assertEquals(decimalNumBar(endTime.plusMinutes(4), 5000), getBar(endIndex - 1))
            assertEquals(decimalNumBar(endTime.plusMinutes(5), 6000), getBar(endIndex - 0))
        }
    }

    @Test
    fun `maximumBarCount 를 나중에 설정해도 효과는 같다`() {
        val series = BaseBarSeries()
        val endTime = ZonedDateTime.now()
        series.addBar(decimalNumBar(endTime.plusMinutes(0), 1000))
        series.addBar(decimalNumBar(endTime.plusMinutes(1), 2000))

        series.maximumBarCount = 1

        assertEquals(1, series.barCount)
        assertEquals(2000, series.lastBar.closePrice.intValue())
    }
}