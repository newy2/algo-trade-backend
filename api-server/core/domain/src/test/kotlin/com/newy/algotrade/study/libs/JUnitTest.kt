package com.newy.algotrade.study.libs

import com.newy.algotrade.study.libs.ta4j.assertDoubleNumEquals
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.ta4j.core.num.DecimalNum
import kotlin.test.assertEquals

@DisplayName("테스트 헬퍼 메소드 테스트")
class JUnitTest {
    @Test
    fun `부동소수점 소수점 2자리 반올림`() {
        assertEquals(0.01, 0.005, 0.005)
        Assertions.assertNotEquals(0.01, 0.004, 0.005)
    }

    @Test
    fun `assertDoubleEquals 헬퍼 메소드`() {
        assertDoubleNumEquals(0.01, DecimalNum.valueOf(0.005))
        assertDoubleNumEquals(0.00, DecimalNum.valueOf(0.004))
    }
}