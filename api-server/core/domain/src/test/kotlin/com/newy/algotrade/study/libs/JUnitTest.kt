package com.newy.algotrade.study.libs

import com.newy.algotrade.study.libs.ta4j.assertDoubleNumEquals
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.ta4j.core.num.DecimalNum
import kotlin.test.assertEquals

@DisplayName("테스트 헬퍼 메소드 테스트")
class JUnitTest {
    @Test
    fun `부동소수점 소수점 2자리 반올림`() {
        assertEquals(0.01, 0.005, 0.005)
        assertNotEquals(0.01, 0.004, 0.005)
    }

    @Test
    fun `assertDoubleEquals 헬퍼 메소드`() {
        assertDoubleNumEquals(0.01, DecimalNum.valueOf(0.005))
        assertDoubleNumEquals(0.00, DecimalNum.valueOf(0.004))
    }

    @Test
    fun `에러 발생 여부 확인 메소드`() {
        assertDoesNotThrow("에러가 발생하지 않아야 한다") {
            assert(true)
        }
        assertThrows<AssertionError>("에러가 발생해야만 한다") {
            assert(false)
        }
    }
}