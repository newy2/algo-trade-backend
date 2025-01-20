package com.newy.algotrade.unit.notification_app.domain

import com.newy.algotrade.notification_app.domain.CodeGenerator
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@DisplayName("알림앱 인증코드 생성기 테스트")
class CodeGeneratorTest {
    private val generator = CodeGenerator()

    @Test
    fun `인증코드는 5글자이다`() {
        assertEquals(5, generator.generate().length)
    }

    @Test
    fun `인증코드는 중복된 글자가 없어야 한다`() {
        assertEquals(5, generator.generate().toSet().size)
    }

    @Test
    fun `인크코드는 CodeGenerator#CODES 의 글자로 만들어져야 한다`() {
        generator.generate().toList().forEach {
            CodeGenerator.CODES.toList().contains(it)
        }
    }

    @Test
    fun `CodeGenerator#CODES 는 숫자와 대문자 알바벳 리스트이다`() {
        assertEquals("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ", CodeGenerator.CODES.joinToString(separator = ""))
    }

    @Test
    fun `인증코드는 이전에 사용한 인증코드를 제외하고 만들 수 있어야 한다`() {
        val beforeUsedCode = "DXG10"
        val code = generator.generate(excludeCode = beforeUsedCode)
        assertNotEquals(code, beforeUsedCode)
    }
}
