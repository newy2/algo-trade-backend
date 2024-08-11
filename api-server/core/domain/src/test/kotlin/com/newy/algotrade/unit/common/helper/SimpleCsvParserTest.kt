package com.newy.algotrade.unit.common.helper

import com.newy.algotrade.domain.common.helper.SimpleCsvParser
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File

const val RESOURCE_PATH = "/csv/for-unit-test-file.csv"
val RESOURCE_CONTENT =
    """
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
        """.trimIndent().split(System.lineSeparator()).stream()

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
        """.trimIndent().split(System.lineSeparator()).stream()

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

    @Test
    fun `파일 객체로 파싱하는 방법`() {
        assertArrayEquals(
            arrayOf(
                arrayOf("1709942400000", "68180.0", "68236.5", "68179.8", "68236.4", "18.308"),
            ),
            SimpleCsvParser.parseFromFile(File(javaClass.getResource(RESOURCE_PATH)!!.file))
        )
    }
}