package com.newy.algotrade.study.kotlin

import com.newy.algotrade.unit.test_helper.RESOURCE_CONTENT
import com.newy.algotrade.unit.test_helper.RESOURCE_PATH
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Duration
import java.util.stream.Collectors
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

class KotlinTest {
    @Test
    fun `Duration 팩토리 메소드는 객체를 캐싱하지 않는다`() {
        Assertions.assertNotSame(Duration.ofMinutes(1), Duration.ofMinutes(1))
        Assertions.assertNotSame(Duration.ofMinutes(1), 1.minutes.toJavaDuration())
    }

    @Test
    fun `BigDecimal 팩토리 메소드는 객체를 캐싱한다`() {
        assertSame(BigDecimal.valueOf(0), BigDecimal.valueOf(0))
        assertSame(BigDecimal.valueOf(0), BigDecimal.ZERO)
        assertSame(BigDecimal.valueOf(0), 0.toBigDecimal())
    }
    
    @Test
    fun `string 을 list 로 변환하는 방법`() {
        val multiLineText = """
            abc
            def
        """.trimIndent()

        assertEquals(listOf("abc", "def"), multiLineText.split("\n"))
    }

    @Test
    fun `list 를 array 로 변환하는 방법`() {
        val list = listOf("abc", "def")

        Assertions.assertArrayEquals(arrayOf("abc", "def"), list.toTypedArray<String>())
        Assertions.assertArrayEquals(
            arrayOf("abc", "def"),
            list.stream().toArray<String> { length -> arrayOfNulls(length) })
    }
}

@DisplayName("resource 폴더의 파일을 읽어오는 방법")
class AccessResourceFolderTest {
    @Test
    fun `리소스 파일 경로는 역슬러시로 시작해야 함`() {
        val correctPath = "/csv/for-unit-test-file.csv"
        val notCorrectPath = "csv/for-unit-test-file.csv"

        correctPath.also {
            Assertions.assertNotNull(javaClass.getResource(it))
            Assertions.assertNotNull(javaClass.getResourceAsStream(it))
        }
        notCorrectPath.also {
            assertNull(javaClass.getResource(it))
            assertNull(javaClass.getResourceAsStream(it))
        }
    }

    @Test
    fun `javaClass#getResource 함수로 읽어오는 방법`() {
        val fromUrl = javaClass.getResource(RESOURCE_PATH)!!.readText()

        assertEquals(RESOURCE_CONTENT, fromUrl)
    }

    @Test
    fun `javaClass#getResourceAsStream 함수로 읽어오는 방법`() {
        val fromStream = javaClass.getResourceAsStream(RESOURCE_PATH)!!
            .bufferedReader()
            .lines()
            .collect(Collectors.joining("\n"))

        assertEquals(RESOURCE_CONTENT, fromStream)
    }
}