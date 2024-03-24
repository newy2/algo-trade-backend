package com.newy.alogotrade.domain.unit.helper

import com.newy.alogotrade.helper.SimpleCsvParser
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.stream.Collectors
import kotlin.test.assertEquals
import kotlin.test.assertNull

const val RESOURCE_PATH = "/csv/for-unit-test-file.csv"
val RESOURCE_CONTENT = """
    startTime, openPrice, highPrice, lowPrice, closePrice, volume
    1709942400000, 68180.0, 68236.5, 68179.8, 68236.4, 18.308
""".trimIndent()

@DisplayName("csv 파일을 문자열 array 로 파싱하기")
class SimpleCsvParserTest {
    @Test
    fun `문자열 stream 으로 파싱`() {
        val inputStream = """
            제목1,제목2
            111,aaa
            222,bbb
        """.trimIndent().split("\n").stream()

        assertArrayEquals(
            arrayOf(
                arrayOf("111", "aaa"),
                arrayOf("222", "bbb"),
            ),
            SimpleCsvParser.parse(inputStream)
        )
    }

    @Test
    fun `빈 문자열이 포함된 경우`() {
        val inputStream = """
            제목1,제목2
            111 , aaa
             222, bbb   
        """.trimIndent().split("\n").stream()

        assertArrayEquals(
            arrayOf(
                arrayOf("111", "aaa"),
                arrayOf("222", "bbb"),
            ),
            SimpleCsvParser.parse(inputStream),
        )
    }

    @Test
    fun `resource 폴더의 파일 경로로 파싱하는 방법`() {
        assertArrayEquals(
            arrayOf(
                arrayOf("1709942400000", "68180.0", "68236.5", "68179.8", "68236.4", "18.308"),
            ),
            SimpleCsvParser.parseFromResource(RESOURCE_PATH)
        )
    }
}

@DisplayName("resource 폴더의 파일을 읽어오는 방법")
class KotlinStudyTest {
    @Test
    fun `리소스 파일 경로는 역슬러시로 시작해야 함`() {
        val correctPath = "/csv/for-unit-test-file.csv"
        val notCorrectPath = "csv/for-unit-test-file.csv"

        correctPath.also {
            assertNotNull(javaClass.getResource(it))
            assertNotNull(javaClass.getResourceAsStream(it))
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

        assertArrayEquals(arrayOf("abc", "def"), list.toTypedArray<String>())
        assertArrayEquals(arrayOf("abc", "def"), list.stream().toArray<String> { length -> arrayOfNulls(length) })
    }
}