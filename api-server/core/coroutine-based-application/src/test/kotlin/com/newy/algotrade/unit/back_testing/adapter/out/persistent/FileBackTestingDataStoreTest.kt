package com.newy.algotrade.unit.back_testing.adapter.out.persistent

import com.newy.algotrade.coroutine_based_application.back_testing.adapter.out.persistent.FileBackTestingDataStore
import com.newy.algotrade.coroutine_based_application.back_testing.domain.BackTestingFileManager
import com.newy.algotrade.coroutine_based_application.back_testing.port.`in`.model.BackTestingDataKey
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.nio.file.Paths
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.test.assertEquals

class FileBackTestingDataStoreTest {
    private val fileManager = BackTestingFileManager()
    private val adapter = FileBackTestingDataStore(fileManager)

    @Test
    fun `없는 파일 - 백테스팅 데이터 가져오기`() = runTest {
        val notExistsFile = BackTestingDataKey(
            productPriceKey = ProductPriceKey(
                Market.BY_BIT,
                ProductType.SPOT,
                "BTCUSDT",
                Duration.ofMinutes(1)
            ),
            searchBeginTime = OffsetDateTime.parse("0000-01-01T00:00Z"),
            searchEndTime = OffsetDateTime.parse("0000-01-01T00:01Z")
        )

        val results = adapter.getBackTestingData(notExistsFile)

        assertEquals(emptyList(), results)
    }

    @Test
    fun `백테스팅 데이터 가져오기`() = runTest {
        val existsFile = BackTestingDataKey(
            productPriceKey = ProductPriceKey(
                Market.BY_BIT,
                ProductType.SPOT,
                "BTCUSDT",
                Duration.ofMinutes(1),
            ),
            searchBeginTime = OffsetDateTime.parse("9999-01-01T00:00+09:00"),
            searchEndTime = OffsetDateTime.parse("9999-01-01T00:01+09:00")
        )

        val results = adapter.getBackTestingData(existsFile)

        assertEquals(
            listOf(
                Candle.TimeFrame.M1(
                    OffsetDateTime.parse("9999-01-01T00:00+09:00"),
                    openPrice = 200.0.toBigDecimal(),
                    highPrice = 1000.0.toBigDecimal(),
                    lowPrice = 100.0.toBigDecimal(),
                    closePrice = 500.0.toBigDecimal(),
                    volume = 10.0.toBigDecimal(),
                )
            ),
            results
        )
    }

    @Test
    fun `백테스팅 데이터 저장하기`() = runTest {
        val key = BackTestingDataKey(
            ProductPriceKey(
                Market.BY_BIT,
                ProductType.SPOT,
                "BTCUSDT",
                Duration.ofMinutes(1)
            ),
            searchBeginTime = OffsetDateTime.parse("8888-01-01T00:00+09:00"),
            searchEndTime = OffsetDateTime.parse("8888-01-01T00:01+09:00"),
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

        adapter.setBackTestingData(key, list)

        assertEquals(adapter.getBackTestingData(key), list)

        Paths.get(fileManager.folderPath(), fileManager.fileName(key)).toFile().deleteOnExit()
    }
}