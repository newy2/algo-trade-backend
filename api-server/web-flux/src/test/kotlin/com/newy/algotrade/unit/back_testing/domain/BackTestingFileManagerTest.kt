package com.newy.algotrade.unit.back_testing.domain

import com.newy.algotrade.back_testing.domain.BackTestingDataKey
import com.newy.algotrade.back_testing.domain.BackTestingFileManager
import com.newy.algotrade.chart.domain.Candle
import com.newy.algotrade.common.domain.consts.ProductType
import helpers.productPriceKey
import org.junit.AfterClass
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue


@DisplayName("저장된 파일 테스트")
class ExistsFileBackTestingFileManagerTest {
    private val key = BackTestingDataKey(
        productPriceKey = productPriceKey(
            productCode = "BTCUSDT",
            interval = Duration.ofMinutes(1)
        ),
        searchBeginTime = OffsetDateTime.parse("9999-01-01T00:00+09:00"),
        searchEndTime = OffsetDateTime.parse("9999-01-01T00:01+09:00")
    )
    private val manager = BackTestingFileManager()

    @Test
    fun `파일 이름`() {
        assertEquals(
            "[BY_BIT][SPOT][BTCUSDT][M1][9999-01-01T00_00+0900 - 9999-01-01T00_01+0900].csv",
            manager.fileName(key)
        )
    }

    @Test
    fun `파일 유무 확인`() {
        assertTrue(manager.hasFile(key))
    }

    @Test
    fun `파일 읽기`() {
        val list = listOf(
            Candle.TimeFrame.M1(
                OffsetDateTime.parse("9999-01-01T00:00+09:00"),
                openPrice = 200.0.toBigDecimal(),
                highPrice = 1000.0.toBigDecimal(),
                lowPrice = 100.0.toBigDecimal(),
                closePrice = 500.0.toBigDecimal(),
                volume = 10.0.toBigDecimal(),
            )
        )

        assertEquals(list, manager.getList(key))
    }
}

@DisplayName("없는 파일 테스트")
class EmptyFileBackTestingFileManagerTest {
    private val key = BackTestingDataKey(
        productPriceKey = productPriceKey(
            productCode = "078020",
            interval = Duration.ofDays(1),
            productType = ProductType.PERPETUAL_FUTURE,
        ),
        searchBeginTime = OffsetDateTime.parse("0000-01-01T00:00Z"),
        searchEndTime = OffsetDateTime.parse("0000-01-01T00:01Z")
    )
    private val manager = BackTestingFileManager()

    @Test
    fun `파일 이름`() {
        assertEquals(
            "[LS_SEC][PERPETUAL_FUTURE][078020][D1][0000-01-01T09_00+0900 - 0000-01-01T09_01+0900].csv",
            manager.fileName(key)
        )
    }

    @Test
    fun `파일 유무 확인`() {
        assertFalse(manager.hasFile(key))
    }

    @Test
    fun `파일 읽기`() {
        val list = manager.getList(key)
        assertEquals(emptyList(), list)
    }
}

@DisplayName("기본 기능 테스트")
class BackTestingFileManagerTest {
    private val key = BackTestingDataKey(
        productPriceKey = productPriceKey(
            productCode = "BTCUSDT",
            interval = Duration.ofMinutes(1)
        ),
        searchBeginTime = OffsetDateTime.parse("8888-01-01T00:00+09:00"),
        searchEndTime = OffsetDateTime.parse("8888-01-01T00:01+09:00"),
    )
    private val manager = BackTestingFileManager()

    @AfterClass
    fun tearDown() {
        File(manager.folderPath(), manager.fileName(key)).deleteOnExit()
    }

    @Test
    fun `폴더 path 확인하기`() {
        assertEquals(javaClass.getResource("/back-testing-source-data")!!.path, manager.folderPath())
    }

    @Test
    fun `파일 쓰기`() {
        val list = listOf(
            Candle.TimeFrame.M1(
                OffsetDateTime.parse("8888-01-01T00:00+09:00"),
                openPrice = 200.0.toBigDecimal(),
                highPrice = 400.0.toBigDecimal(),
                lowPrice = 100.0.toBigDecimal(),
                closePrice = 300.0.toBigDecimal(),
                volume = 50.0.toBigDecimal(),
            )
        )

        manager.setList(key, list)

        assertEquals(list, manager.getList(key))
    }

    @Test
    fun `setList 는 파일을 덮어쓴다`() {
        manager.setList(
            key, listOf(
                Candle.TimeFrame.M1(
                    OffsetDateTime.parse("8888-01-01T00:00+09:00"),
                    openPrice = 2000.0.toBigDecimal(),
                    highPrice = 4000.0.toBigDecimal(),
                    lowPrice = 1000.0.toBigDecimal(),
                    closePrice = 3000.0.toBigDecimal(),
                    volume = 500.0.toBigDecimal(),
                )
            )
        )

        val replacedList = listOf(
            Candle.TimeFrame.M1(
                OffsetDateTime.parse("8888-01-01T00:00+09:00"),
                openPrice = 200.0.toBigDecimal(),
                highPrice = 400.0.toBigDecimal(),
                lowPrice = 100.0.toBigDecimal(),
                closePrice = 300.0.toBigDecimal(),
                volume = 50.0.toBigDecimal(),
            )
        )
        manager.setList(key, replacedList)

        assertEquals(replacedList, manager.getList(key))
    }
}