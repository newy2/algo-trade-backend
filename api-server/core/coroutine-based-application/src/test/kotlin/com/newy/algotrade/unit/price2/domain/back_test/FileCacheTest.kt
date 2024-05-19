package com.newy.algotrade.unit.price2.domain.back_test

import com.newy.algotrade.coroutine_based_application.price2.domain.back_test.BackTestDataLoader
import com.newy.algotrade.coroutine_based_application.price2.domain.back_test.FileCache
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.test.assertEquals

class FileCacheTest {
    private val cache = FileCache()

    @Test
    fun `캐쉬 파일이 없는 경우`() {
        val noFileKey = BackTestDataLoader.Key(
            ProductPriceKey(
                Market.BY_BIT,
                ProductType.SPOT,
                "BTCUSDT",
                Duration.ofMinutes(1)
            ),
            startDateTime = OffsetDateTime.parse("0000-01-01T00:00Z"),
            endDateTime = OffsetDateTime.parse("0000-01-01T00:01Z"),
        )

        assertEquals(emptyList(), cache.load(noFileKey))
    }

    @Test
    fun `캐시 저장 후 불러오기`() {
        val key = BackTestDataLoader.Key(
            ProductPriceKey(
                Market.BY_BIT,
                ProductType.SPOT,
                "BTCUSDT",
                Duration.ofMinutes(1)
            ),
            startDateTime = OffsetDateTime.parse("9999-01-01T00:00Z"),
            endDateTime = OffsetDateTime.parse("9999-01-01T00:01Z"),
        )
        val list = listOf(
            Candle.TimeFrame.M1(
                OffsetDateTime.parse("9999-01-01T00:00Z"),
                openPrice = 200.0.toBigDecimal(),
                highPrice = 1000.0.toBigDecimal(),
                lowPrice = 100.0.toBigDecimal(),
                closePrice = 500.0.toBigDecimal(),
                volume = 10.0.toBigDecimal(),
            )
        )

        cache.insert(key, list)

        assertEquals(list, cache.load(key))
    }

    @Test
    fun `KST 타이존 포맷 - 캐시 저장 후 불러오기`() {
        val zoneOffsetString = "+09:00"

        val key = BackTestDataLoader.Key(
            ProductPriceKey(
                Market.BY_BIT,
                ProductType.SPOT,
                "BTCUSDT",
                Duration.ofMinutes(1)
            ),
            startDateTime = OffsetDateTime.parse("9999-01-01T00:00$zoneOffsetString"),
            endDateTime = OffsetDateTime.parse("9999-01-01T00:01$zoneOffsetString"),
        )
        val list = listOf(
            Candle.TimeFrame.M1(
                OffsetDateTime.parse("9999-01-01T00:00$zoneOffsetString"),
                openPrice = 200.0.toBigDecimal(),
                highPrice = 1000.0.toBigDecimal(),
                lowPrice = 100.0.toBigDecimal(),
                closePrice = 500.0.toBigDecimal(),
                volume = 10.0.toBigDecimal(),
            )
        )

        cache.insert(key, list)

        assertEquals(list, cache.load(key))
    }
}
