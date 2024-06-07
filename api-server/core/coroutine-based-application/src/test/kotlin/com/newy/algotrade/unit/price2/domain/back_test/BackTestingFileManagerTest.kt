package com.newy.algotrade.unit.price2.domain.back_test

import com.newy.algotrade.coroutine_based_application.price2.domain.back_test.BackTestingFileManager
import com.newy.algotrade.coroutine_based_application.price2.port.`in`.model.BackTestingDataKey
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import org.junit.AfterClass
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.nio.file.Paths
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue


@DisplayName("저장된 파일 테스트")
class ExistsFileBackTestingFileManagerTest {
    private val key = BackTestingDataKey(
        ProductPriceKey(
            Market.BY_BIT,
            ProductType.SPOT,
            "BTCUSDT",
            Duration.ofMinutes(1),
        ),
        OffsetDateTime.parse("9999-01-01T00:00+09:00"),
        OffsetDateTime.parse("9999-01-01T00:01+09:00")
    )
    private val manager = BackTestingFileManager()

    @Test
    fun `파일 이름`() {
        assertEquals(
            "[BY_BIT][SPOT][BTCUSDT][M1][9999-01-01T00:00+09:00 - 9999-01-01T00:01+09:00].csv",
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
        ProductPriceKey(
            Market.E_BEST,
            ProductType.PERPETUAL_FUTURE,
            "078020",
            Duration.ofDays(1),
        ),
        OffsetDateTime.parse("0000-01-01T00:00Z"),
        OffsetDateTime.parse("0000-01-01T00:01Z")
    )
    private val manager = BackTestingFileManager()

    @Test
    fun `파일 이름`() {
        assertEquals(
            "[E_BEST][PERPETUAL_FUTURE][078020][D1][0000-01-01T09:00+09:00 - 0000-01-01T09:01+09:00].csv",
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
        ProductPriceKey(
            Market.BY_BIT,
            ProductType.SPOT,
            "BTCUSDT",
            Duration.ofMinutes(1)
        ),
        searchBeginTime = OffsetDateTime.parse("8888-01-01T00:00+09:00"),
        searchEndTime = OffsetDateTime.parse("8888-01-01T00:01+09:00"),
    )
    private val manager = BackTestingFileManager()

    @AfterClass
    fun tearDown() {
        Paths.get(manager.folderPath(), manager.fileName(key)).toFile().deleteOnExit()
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

        assertEquals(manager.getList(key), list)
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

        assertEquals(manager.getList(key), list)
    }
}