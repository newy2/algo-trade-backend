package com.newy.algotrade.domain.study.library.ta4j

import org.junit.jupiter.api.*
import org.ta4j.core.Bar
import org.ta4j.core.BaseBar
import org.ta4j.core.num.DecimalNum
import org.ta4j.core.num.DoubleNum
import org.ta4j.core.num.Num
import java.time.Duration
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

@DisplayName("BaseBar 객체의 필드 용도 설명")
class BaseBarFieldTest {
    @Test
    fun `가격 관련 필드 설명`() {
        val openPrice = 1000.toBigDecimal()
        val highPrice = 1100.toBigDecimal()
        val lowPrice = 900.toBigDecimal()
        val closePrice = 1000.toBigDecimal()
        val volume = 4.toBigDecimal()

        val bar = BaseBar(
            1.minutes.toJavaDuration(),
            ZonedDateTime.now(),
            openPrice,
            highPrice,
            lowPrice,
            closePrice,
            volume,
        )

        assertEquals(1000, bar.openPrice.intValue(), "[필수] 시가")
        assertEquals(1100, bar.highPrice.intValue(), "[필수] 고가")
        assertEquals(900, bar.lowPrice.intValue(), "[필수] 저가")
        assertEquals(1000, bar.closePrice.intValue(), "[필수] 종가(또는 현재가)")
        assertEquals(4, bar.volume.intValue(), "[필수] 거래량")
        assertEquals(0, bar.amount.intValue(), "[선택] 거래대금 = 거래량 * 거래금액") //참고: https://github.com/ta4j/ta4j/issues/441
        assertEquals(0, bar.trades.toInt(), "[선택] 거래횟수")
    }

    @Test
    fun `시간 관련 필드 설명`() {
        val timePeriod = 1.minutes.toJavaDuration()
        val endTime = ZonedDateTime.parse("2024-03-09T00:01Z")

        val bar = BaseBar(
            timePeriod,
            endTime,
            DecimalNum::valueOf
        )

        assertEquals("2024-03-09T00:00Z", bar.beginTime.toString(), "'시작시간'은 timePeriod(1분)로 계산함")
        assertEquals("2024-03-09T00:01Z", bar.endTime.toString())
    }

    @Test
    fun `주의 - '고가'와 '저가'를 반대로 입력한 경우`() {
        assertDoesNotThrow("아무런 에러가 발생하지 않는다") {
            val highPrice = 500.toBigDecimal()
            val lowPrice = 1500.toBigDecimal()
            val bar = BaseBar(
                1.minutes.toJavaDuration(),
                ZonedDateTime.now(),
                1000.toBigDecimal(),
                highPrice,
                lowPrice,
                1000.toBigDecimal(),
                0.toBigDecimal(),
            )

            assertEquals(500, bar.highPrice.intValue(), "고가")
            assertEquals(1500, bar.lowPrice.intValue(), "저가")
        }
    }
}

@DisplayName("Ta4j 의 Num 객체 테스트")
class Ta4jNumTest {
    @Test
    fun `Num 의 구현체는 'DoubleNum' 과 'DecimalNum' 이 있음`() {
        val doubleNum: Num = DoubleNum.valueOf(1000)
        val decimalNum: Num = DecimalNum.valueOf(1000)

        assertEquals(doubleNum, doubleNum)
        assertNotEquals(decimalNum, doubleNum)
    }

    @Test
    fun `소수점 계산 정확도`() {
        assertEquals(0.09999999999999998, DoubleNum.valueOf(1).minus(DoubleNum.valueOf(0.9)).doubleValue())
        assertEquals(0.1, DecimalNum.valueOf(1).minus(DecimalNum.valueOf(0.9)).doubleValue())
    }

    @Test
    fun `DoubleNum 과 DecimalNum 을 섞어서 계산식을 호출할 수 없음`() {
        assertThrows<ClassCastException> {
            DoubleNum.valueOf(1).minus(DecimalNum.valueOf(0.9))
        }
        assertThrows<ClassCastException> {
            DecimalNum.valueOf(1).minus(DoubleNum.valueOf(0.9))
        }
    }
}

@DisplayName("BaseBar 생성자별 기본 Num 클래스 확인")
class BaseBarDefaultNumClassTest {
    private lateinit var duration: Duration
    private lateinit var endTime: ZonedDateTime

    @BeforeEach
    fun setUp() {
        duration = 1.minutes.toJavaDuration()
        endTime = ZonedDateTime.now()
    }

    @Test
    fun `생성자의 가격 관련 파라미터 타입이 BigDecimal 인 경우 - DecimalNum`() {
        val bar = 1000.toBigDecimal().let { bigDecimal ->
            BaseBar(
                duration,
                endTime,
                bigDecimal,
                bigDecimal,
                bigDecimal,
                bigDecimal,
                bigDecimal,
            )
        }

        assertTrue(bar.openPrice is DecimalNum)
    }

    @Test
    fun `생성자의 가격 관련 파라미터 타입이 String 인 경우 - DecimalNum`() {
        val bar = "1000".let { string ->
            BaseBar(
                duration,
                endTime,
                string,
                string,
                string,
                string,
                string,
            )
        }

        assertTrue(bar.openPrice is DecimalNum)
    }

    @Test
    fun `생성자의 가격 관련 파라미터 타입이 Double 인 경우 - DoubleNum`() {
        val bar = 1000.toDouble().let { double ->
            BaseBar(
                duration,
                endTime,
                double,
                double,
                double,
                double,
                double,
            )
        }

        assertTrue(bar.openPrice is DoubleNum)
    }

    @Test
    fun `생성자의 가격 관련 파라미터 타입과 상관없이, Num 타입을 직접 지정할 수도 있음`() {
        val bar = 1000.toDouble().let { double ->
            BaseBar(
                duration,
                endTime,
                double,
                double,
                double,
                double,
                double,
                double,
                0.toLong(),
                DecimalNum::valueOf
            )
        }

        assertTrue(bar.openPrice is DecimalNum)
    }
}

@DisplayName("BaseBar 동등성 테스트")
class BaseBarEqualityTest {
    @Test
    fun `BaseBar 객체 동등성 비교는 Num 타입까지 같아야 한다`() {
        val duration = 1.minutes.toJavaDuration()
        val endTime = ZonedDateTime.now()

        val decimalNum1 = 1000.toBigDecimal().let { bigDecimal ->
            BaseBar(duration, endTime, bigDecimal, bigDecimal, bigDecimal, bigDecimal, bigDecimal)
        }
        val decimalNum2 = "1000".let { string ->
            BaseBar(duration, endTime, string, string, string, string, string)
        }
        val doubleNum = 1000.toDouble().let { double ->
            BaseBar(duration, endTime, double, double, double, double, double)
        }

        assertEquals(decimalNum1, decimalNum2)
        assertNotEquals(doubleNum, decimalNum1)
    }
}

@DisplayName("BaseBar 의 '종가(현재가)'와 '거래량'를 변경하는 방법")
class ChangeBaseBarDataTest {
    private lateinit var bar: Bar

    @BeforeEach
    fun setUp() {
        bar = BaseBar(
            1.minutes.toJavaDuration(),
            ZonedDateTime.now(),
            1000.toBigDecimal(),
            1000.toBigDecimal(),
            1000.toBigDecimal(),
            1000.toBigDecimal(),
            0.toBigDecimal(),
        )
    }

    @Test
    fun `종가(현재가)가 변하지 않은 경우`() {
        bar.addPrice(DecimalNum.valueOf(1000))

        assertEquals(1000, bar.openPrice.intValue())
        assertEquals(1000, bar.highPrice.intValue())
        assertEquals(1000, bar.lowPrice.intValue())
        assertEquals(1000, bar.closePrice.intValue(), "종가 변경 없음")
        assertEquals(0, bar.amount.intValue())
        assertEquals(0, bar.volume.intValue())
        assertEquals(0, bar.trades.toInt())
    }

    @Test
    fun `종가(현재가)가 상승한 경우`() {
        bar.addPrice(DecimalNum.valueOf(1500))

        assertEquals(1000, bar.openPrice.intValue())
        assertEquals(1500, bar.highPrice.intValue(), "고가 업데이트")
        assertEquals(1000, bar.lowPrice.intValue())
        assertEquals(1500, bar.closePrice.intValue(), "종가 업데이트")
        assertEquals(0, bar.amount.intValue())
        assertEquals(0, bar.volume.intValue())
        assertEquals(0, bar.trades.toInt())
    }

    @Test
    fun `종가(현재가)가 하락한 경우`() {
        bar.addPrice(DecimalNum.valueOf(500))

        assertEquals(1000, bar.openPrice.intValue())
        assertEquals(1000, bar.highPrice.intValue())
        assertEquals(500, bar.lowPrice.intValue(), "저가 업데이트")
        assertEquals(500, bar.closePrice.intValue(), "종가 업데이트")
        assertEquals(0, bar.amount.intValue())
        assertEquals(0, bar.volume.intValue())
        assertEquals(0, bar.trades.toInt())
    }

    @Test
    fun `거래가 발생한 경우`() {
        bar.addTrade(DecimalNum.valueOf(2), DecimalNum.valueOf(1000))

        assertEquals(1000, bar.openPrice.intValue())
        assertEquals(1000, bar.highPrice.intValue())
        assertEquals(1000, bar.lowPrice.intValue())
        assertEquals(1000, bar.closePrice.intValue())
        assertEquals(2 * 1000, bar.amount.intValue(), "거래대금(거래량 * 거래금액) 업데이트")
        assertEquals(2, bar.volume.intValue(), "거래량 업데이트")
        assertEquals(1, bar.trades.toInt(), "거래횟수 업데이트")
    }

    @Test
    fun `종가(현재가)가 상승하면서 거래가 발생한 경우`() {
        bar.addTrade(DecimalNum.valueOf(2), DecimalNum.valueOf(1500))

        assertEquals(1000, bar.openPrice.intValue())
        assertEquals(1500, bar.highPrice.intValue(), "고가 업데이트")
        assertEquals(1000, bar.lowPrice.intValue())
        assertEquals(1500, bar.closePrice.intValue(), "종가 업데이트")
        assertEquals(2 * 1500, bar.amount.intValue(), "거래대금 업데이트")
        assertEquals(2, bar.volume.intValue(), "거래량 업데이트")
        assertEquals(1, bar.trades.toInt(), "거래횟수 업데이트")
    }

    @Test
    fun `종가(현재가)가 변경되면서 여러 번 거래가 발생한 경우`() {
        bar.addTrade(DecimalNum.valueOf(2), DecimalNum.valueOf(500))
        bar.addTrade(DecimalNum.valueOf(3), DecimalNum.valueOf(1500))

        assertEquals(1000, bar.openPrice.intValue())
        assertEquals(1500, bar.highPrice.intValue(), "고가 업데이트")
        assertEquals(500, bar.lowPrice.intValue(), "저가 업데이트")
        assertEquals(1500, bar.closePrice.intValue(), "종가 업데이트")
        assertEquals((2 * 500) + (3 * 1500), bar.amount.intValue(), "거래대금 업데이트")
        assertEquals(5, bar.volume.intValue(), "거래량 업데이트")
        assertEquals(2, bar.trades.toInt(), "거래횟수 업데이트")
    }

    @Test
    fun `다른 타입의 Num 객체로 종가(현재가)를 변경하는 경우`() {
        assertThrows<ClassCastException> {
            val otherTypePrice = DoubleNum.valueOf(500)
            bar.addPrice(otherTypePrice)
        }
        assertDoesNotThrow {
            val sameTypePrice = DecimalNum.valueOf(500)
            bar.addPrice(sameTypePrice)
        }
    }

    @Test
    fun `다른 타입의 Num 객체로 거래가 발생한 경우`() {
        assertThrows<ClassCastException> {
            val otherTypeVolume = DoubleNum.valueOf(2)
            val otherTypeAmount = DoubleNum.valueOf(1500)
            bar.addTrade(otherTypeVolume, otherTypeAmount)
        }
        assertThrows<ClassCastException> {
            val sameTypeVolume = DecimalNum.valueOf(2)
            val otherTypeAmount = DoubleNum.valueOf(1500)
            bar.addTrade(sameTypeVolume, otherTypeAmount)
        }
        assertThrows<ClassCastException> {
            val otherTypeVolume = DoubleNum.valueOf(2)
            val sameTypeAmount = DecimalNum.valueOf(1500)
            bar.addTrade(otherTypeVolume, sameTypeAmount)
        }
        assertDoesNotThrow {
            val sameTypeVolume = DecimalNum.valueOf(2)
            val sameTypeAmount = DecimalNum.valueOf(1500)
            bar.addTrade(sameTypeVolume, sameTypeAmount)
        }
    }
}