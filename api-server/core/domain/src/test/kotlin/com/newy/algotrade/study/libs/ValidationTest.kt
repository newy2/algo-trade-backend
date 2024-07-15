package com.newy.algotrade.study.libs

import com.newy.algotrade.domain.common.libs.validation.NotBlankElements
import jakarta.validation.*
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ValidateTest {
    private val validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `validator 는 전역 객체가 아님`() {
        val validator1 = Validation.buildDefaultValidatorFactory().validator
        val validator2 = Validation.buildDefaultValidatorFactory().validator

        assertEquals(validator1, validator1)
        assertNotEquals(validator1, validator2)
    }

    @Test
    fun `NotBlank 애너테이션`() {
        class TestDto(
            @field:NotBlank val name: String,
        )

        assertEquals(0, validator.validate(TestDto("abc")).size)
        assertEquals(1, validator.validate(TestDto("")).size)
        assertEquals(1, validator.validate(TestDto(" ")).size)
        assertEquals(1, validator.validate(TestDto("  ")).size)
        validator.validate(TestDto("")).let { results ->
            assertEquals(1, results.size)
            results.first().let {
                assertEquals("공백일 수 없습니다", it.message)
                assertEquals("name", it.propertyPath.toString())
            }
        }
    }

    @Test
    fun `Pattern 애너테이션`() {
        class TestDto(
            @field:Pattern(regexp = "BY_BIT|LS_SEC") val market: String
        )

        assertEquals(0, validator.validate(TestDto("BY_BIT")).size)
        assertEquals(0, validator.validate(TestDto("LS_SEC")).size)
        validator.validate(TestDto("not market name")).let { results ->
            assertEquals(1, results.size)
            results.first().let {
                assertEquals("\"BY_BIT|LS_SEC\"와 일치해야 합니다", it.message)
                assertEquals("market", it.propertyPath.toString())
            }
        }
    }

    @Test
    fun `Min 애너테이션`() {
        class TestDto(
            @field:Min(1) val number: Long
        )

        assertEquals(0, validator.validate(TestDto(1)).size)
        assertEquals(0, validator.validate(TestDto(2)).size)
        validator.validate(TestDto(0)).let { results ->
            assertEquals(1, results.size)
            results.first().let {
                assertEquals("1 이상이어야 합니다", it.message)
                assertEquals("number", it.propertyPath.toString())
            }
        }
    }
}

class CustomValidateAnnotationTest {
    private val validator = Validation.buildDefaultValidatorFactory().validator
    
    @Test
    fun `리스트 element 검증`() {
        class TestDto(
            @field:NotBlankElements val list: List<String>
        )

        assertEquals(0, validator.validate(TestDto(emptyList())).size)
        assertEquals(0, validator.validate(TestDto(listOf("a"))).size)
        assertEquals(1, validator.validate(TestDto(listOf(""))).size)
        assertEquals(1, validator.validate(TestDto(listOf(" "))).size)
    }
}