package com.newy.algotrade.unit.market_account.service

import com.newy.algotrade.market_account.service.RegisterMarketAccountCommandService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.transaction.annotation.Transactional
import kotlin.reflect.full.functions
import kotlin.reflect.full.hasAnnotation
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@DisplayName("애너테이션 사용 여부 테스트")
class RegisterMarketAccountCommandServiceAnnotationTest {
    @Test
    fun `checkDuplicateMarketAccount 메서드는 @Transactional 애너테이션을 선언해야 한다`() {
        val method =
            RegisterMarketAccountCommandService::class.functions.find { it.name == "checkDuplicateMarketAccount" }!!

        assertTrue(method.hasAnnotation<Transactional>())
        assertTrue((method.annotations.get(0) as Transactional).readOnly)
    }

    @Test
    fun `validMarketAccount 메서드는 @Transactional 애너테이션을 선언하지 않아야 한다`() {
        val method =
            RegisterMarketAccountCommandService::class.functions.find { it.name == "validMarketAccount" }!!

        assertFalse(method.hasAnnotation<Transactional>())
    }

    @Test
    fun `saveMarketAccount 메서드는 @Transactional 애너테이션을 선언해야 한다`() {
        val method =
            RegisterMarketAccountCommandService::class.functions.find { it.name == "saveMarketAccount" }!!

        assertTrue(method.hasAnnotation<Transactional>())
        assertFalse((method.annotations.get(0) as Transactional).readOnly)
    }
}