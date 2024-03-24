package com.newy.alogotrade.domain.study.library.ta4j

import org.junit.jupiter.api.*
import org.ta4j.core.BarSeries
import org.ta4j.core.BaseBar
import org.ta4j.core.BaseBarSeries
import org.ta4j.core.num.DecimalNum
import org.ta4j.core.num.DoubleNum
import java.math.BigDecimal
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

fun decimalNumBar(endTime: ZonedDateTime, price: BigDecimal = 1000.toBigDecimal()) = BaseBar(
    1.minutes.toJavaDuration(),
    endTime,
    price,
    price,
    price,
    price,
    0.toBigDecimal(),
)

fun doubleNumBar(endTime: ZonedDateTime, price: Double = 1000.toDouble()) = BaseBar(
    1.minutes.toJavaDuration(),
    endTime,
    price,
    price,
    price,
    price,
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
        val notDecimalNumBar = doubleNumBar(ZonedDateTime.now())
        val notDoubleNumBar = decimalNumBar(ZonedDateTime.now())

        assertThrows<IllegalArgumentException>("다른 Num 타입의 Bar 를 추가한 경우") {
            decimalNumBarSeries.addBar(notDecimalNumBar)
        }
        assertThrows<IllegalArgumentException>("다른 Num 타입의 Bar 를 추가한 경우") {
            doubleNumBarSeries.addBar(notDoubleNumBar)
        }
    }
}


@DisplayName("BaseBar 에 Bar 추가하는 방법")
class BaseBarSeriesAddBarTest {
    private lateinit var series: BarSeries
    private lateinit var endTime: ZonedDateTime

    @BeforeEach
    fun setUp() {
        series = BaseBarSeries()
        endTime = ZonedDateTime.now()

        series.addBar(decimalNumBar(endTime, 1000.toBigDecimal()))
    }

    @Test
    fun `Bar 추가하기`() {
        series.addBar(decimalNumBar(endTime.plusMinutes(1), 2000.toBigDecimal()))
        series.addBar(decimalNumBar(endTime.plusMinutes(2), 3000.toBigDecimal()))

        assertEquals(3, series.barCount)
        series.run {
            assertEquals(1000, getBar(beginIndex).closePrice.intValue(), "첫 변째 Bar 에 접근하는 방법1")
            assertEquals(1000, firstBar.closePrice.intValue(), "첫 변째 Bar 에 접근하는 방법2")
        }
        series.run {
            assertEquals(3000, getBar(endIndex).closePrice.intValue(), "마지막 Bar 에 접근하는 방법1")
            assertEquals(3000, lastBar.closePrice.intValue(), "마지막 Bar 에 접근하는 방법2")
        }
    }

    @Test
    fun `새로 추가하는 Bar 의 endTime 은 마지막 Bar 의 endTime 보다 커야한다`() {
        assertThrows<IllegalArgumentException>("마지막 endTime 과 같은 경우") {
            series.addBar(decimalNumBar(endTime))
        }
        assertThrows<IllegalArgumentException>("마지막 endTime 보다 과거인 경우") {
            series.addBar(decimalNumBar(endTime.minusMinutes(1)))
        }
    }

    @Test
    fun `BarSeries 의 마지막 Bar 데이터(종가, 거래량 등)를 업데이트 하는 방법`() {
        val isReplace = true
        series.addBar(decimalNumBar(endTime, 2000.toBigDecimal()), isReplace)

        assertEquals(1, series.barCount)
        assertEquals(2000, series.lastBar.closePrice.intValue())
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

        series.addBar(decimalNumBar(endTime.plusMinutes(0), 1000.toBigDecimal()))
        series.addBar(decimalNumBar(endTime.plusMinutes(1), 2000.toBigDecimal()))
        series.addBar(decimalNumBar(endTime.plusMinutes(2), 3000.toBigDecimal()))
        series.addBar(decimalNumBar(endTime.plusMinutes(3), 4000.toBigDecimal()))
        series.addBar(decimalNumBar(endTime.plusMinutes(4), 5000.toBigDecimal()))
        series.addBar(decimalNumBar(endTime.plusMinutes(5), 6000.toBigDecimal()))
    }

    @Test
    fun `BarSeries 상태 값 설명`() {
        series.run {
            assertEquals(3, barCount, "등록된 Bar 갯수")
            assertEquals(3, removedBarsCount, "삭제된 Bar 갯수")
            assertEquals(0, beginIndex, "첫 번째 Bar index")
            assertEquals(5, endIndex, "마지막 Bar index. endIndex 의 값은 ('barCount' + 'removedBarsCount' - 1) 이다.")
            assertEquals(endIndex, barCount + removedBarsCount - 1, "endIndex 계산식")
        }
    }

    @Test
    fun `BarSeries 에 등록된 Bar 는 FIFO 방식으로 제거된다`() {
        series.run {
            assertEquals(4000, firstBar.closePrice.intValue())
            assertEquals(4000, getBar(beginIndex).closePrice.intValue())
        }
        series.run {
            assertEquals(6000, lastBar.closePrice.intValue())
            assertEquals(6000, getBar(endIndex).closePrice.intValue())
        }
    }

    @Test
    fun `BarSeries#getBar 함수를 사용시, index 범위를 벗어난 경우 에러가 발생한다`() {
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
            assertEquals(4000, getBar(endIndex - 5).closePrice.intValue())
            assertEquals(4000, getBar(endIndex - 4).closePrice.intValue(), "특이 케이스: firstBar 리턴")
            assertEquals(4000, getBar(endIndex - 3).closePrice.intValue(), "특이 케이스: firstBar 리턴")
            assertEquals(4000, getBar(endIndex - 2).closePrice.intValue(), "특이 케이스: firstBar 리턴")
            assertEquals(5000, getBar(endIndex - 1).closePrice.intValue())
            assertEquals(6000, getBar(endIndex - 0).closePrice.intValue())
        }
    }

    @Test
    fun `maximumBarCount 를 나중에 설정해도 효과는 같다`() {
        val series = BaseBarSeries()
        val endTime = ZonedDateTime.now()
        series.addBar(decimalNumBar(endTime.plusMinutes(0), 1000.toBigDecimal()))
        series.addBar(decimalNumBar(endTime.plusMinutes(1), 2000.toBigDecimal()))

        series.maximumBarCount = 1

        assertEquals(1, series.barCount)
        assertEquals(2000, series.lastBar.closePrice.intValue())
    }
}