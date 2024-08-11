package com.newy.algotrade.unit.back_testing.adapter.out.persistence

import com.newy.algotrade.coroutine_based_application.back_testing.adapter.out.persistence.BackTestingDataFileStorageAdapter
import com.newy.algotrade.domain.back_testing.BackTestingDataKey
import com.newy.algotrade.domain.back_testing.BackTestingFileManager
import com.newy.algotrade.domain.chart.Candle
import helpers.productPriceKey
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.io.File
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.test.assertEquals

class BackTestingDataFileStorageAdapterTest {
    private val productPriceKey = productPriceKey(
        productCode = "BTCUSDT",
        interval = Duration.ofMinutes(1)
    )
    private val fileManager = BackTestingFileManager()
    private val adapter = BackTestingDataFileStorageAdapter(fileManager)

    @Test
    fun `없는 파일 - 백테스팅 데이터 가져오기`() = runTest {
        val notExistsFile = BackTestingDataKey(
            productPriceKey = productPriceKey,
            searchBeginTime = OffsetDateTime.parse("0000-01-01T00:00Z"),
            searchEndTime = OffsetDateTime.parse("0000-01-01T00:01Z")
        )

        val results = adapter.getBackTestingData(notExistsFile)

        assertEquals(emptyList(), results)
    }

    @Test
    fun `백테스팅 데이터 가져오기`() = runTest {
        val existsFile = BackTestingDataKey(
            productPriceKey = productPriceKey,
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
            productPriceKey = productPriceKey,
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

        File(fileManager.folderPath(), fileManager.fileName(key)).deleteOnExit()
    }
}