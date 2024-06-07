package com.newy.algotrade.unit.common.helper

import com.newy.algotrade.domain.common.helper.SimpleCsvParser
import com.newy.algotrade.domain.common.helper.SimpleCsvWriter
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File

class SimpleCsvWriterTest {
    private lateinit var tempFile: File

    @BeforeEach
    fun setUp() {
        tempFile = File.createTempFile("temp_file", ".tmp")
    }

    @AfterEach
    fun tearDown() {
        tempFile.deleteOnExit()
    }

    @Test
    fun `CSV 파일 쓰기 테스트`() {
        val titles = listOf("startTime", "openPrice", "highPrice", "lowPrice", "closePrice", "volume")
        val contents = listOf(listOf(1714489200000, 200.0, 1000.0, 100.0, 500.0, 10.0))

        SimpleCsvWriter.write(tempFile, titles, contents)

        SimpleCsvParser.parseFromFile(tempFile).let { parsed ->
            assertEquals(1, parsed.size)
            parsed.first().let {
                assertEquals("1714489200000", it[0])
                assertEquals("200.0", it[1])
                assertEquals("1000.0", it[2])
                assertEquals("100.0", it[3])
                assertEquals("500.0", it[4])
                assertEquals("10.0", it[5])
            }
        }
    }

    @Test
    fun `titles 길이와 contents 길이가 다른 경우`() {
        val titles = listOf("title1", "title2")
        val contents = listOf(listOf("content1"))

        assertThrows<IllegalArgumentException> {
            SimpleCsvWriter.write(tempFile, titles, contents)
        }
    }

    @Test
    fun `contents 가 없는 경우`() {
        val titles = listOf("title1", "title2")
        val contents = emptyList<List<Any>>()

        assertThrows<IllegalArgumentException> {
            SimpleCsvWriter.write(tempFile, titles, contents)
        }
    }
}