package com.newy.algotrade.integration.product.application.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.coroutine_based_application.auth.adpter.out.web.EBestAccessTokenHttpApi
import com.newy.algotrade.coroutine_based_application.common.web.default_implement.DefaultHttpApiClient
import com.newy.algotrade.coroutine_based_application.product.adapter.out.web.FetchByBitProductPrice
import com.newy.algotrade.coroutine_based_application.product.adapter.out.web.FetchEBestProductPrice
import com.newy.algotrade.coroutine_based_application.product.adapter.out.web.FetchProductPriceProxy
import com.newy.algotrade.coroutine_based_application.product.application.service.CreateBackTestingDataService
import com.newy.algotrade.coroutine_based_application.product.port.`in`.model.BackTestingDataKey
import com.newy.algotrade.domain.auth.adapter.out.common.model.PrivateApiInfo
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.common.mapper.JsonConverterByJackson
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import helpers.TestEnv
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.Duration
import java.time.OffsetDateTime
import java.util.*
import kotlin.test.assertEquals

private fun eBestHttpApiClient() =
    DefaultHttpApiClient(
        OkHttpClient(),
        TestEnv.EBest.url,
        JsonConverterByJackson(jacksonObjectMapper())
    )

private fun byBitHttpApiClient() =
    DefaultHttpApiClient(
        OkHttpClient(),
        TestEnv.ByBit.url,
        JsonConverterByJackson(jacksonObjectMapper())
    )

private fun loadProductPriceProxy() =
    FetchProductPriceProxy(
        mapOf(
            Market.E_BEST to FetchEBestProductPrice(
                eBestHttpApiClient(),
                EBestAccessTokenHttpApi(eBestHttpApiClient()),
                PrivateApiInfo(
                    key = TestEnv.EBest.apiKey,
                    secret = TestEnv.EBest.apiSecret,
                )
            ),
            Market.BY_BIT to FetchByBitProductPrice(
                byBitHttpApiClient()
            )
        )
    )

@DisplayName("이베스트 백테스팅 데이터 조회 테스트")
class EBestCreateBackTestingDataServiceTest {
    private val service = CreateBackTestingDataService(loadProductPriceProxy())
    private val productPriceKey = ProductPriceKey(
        Market.E_BEST,
        ProductType.SPOT,
        "005930",
        Duration.ofMinutes(1),
    )

    @Test
    fun `조회하는 시간이 장시작 이전인 경우`() = runTest {
        val beginTime = OffsetDateTime.parse("2024-05-31T08:59+09:00")
        val endTime = OffsetDateTime.parse("2024-05-31T09:00+09:00")
        val seedSize = 0

        val list = service.createData(BackTestingDataKey(productPriceKey, beginTime, endTime), seedSize)

        assertEquals(emptyList(), list)
    }

    @Test
    fun `조회 시작시간과 종료시간이 같은 경우`() = runTest {
        val beginTime = OffsetDateTime.parse("2024-05-31T09:00+09:00")
        val endTime = OffsetDateTime.parse("2024-05-31T09:00+09:00")
        val seedSize = 0

        val list = service.createData(BackTestingDataKey(productPriceKey, beginTime, endTime), seedSize)

        assertEquals(emptyList(), list)
    }

    @Test
    fun `1개 데이터 조회`() = runTest {
        val beginTime = OffsetDateTime.parse("2024-05-31T09:00+09:00")
        val endTime = OffsetDateTime.parse("2024-05-31T09:01+09:00")
        val seedSize = 0

        val list = service.createData(BackTestingDataKey(productPriceKey, beginTime, endTime), seedSize)

        assertEquals(1, list.size)
        assertTrue(OffsetDateTime.parse("2024-05-31T09:00+09:00").isEqual(list.first().time.begin))
    }

    @Test
    fun `seedSize 가 있는 경우`() = runTest {
        val beginTime = OffsetDateTime.parse("2024-05-31T09:00+09:00")
        val endTime = OffsetDateTime.parse("2024-05-31T09:01+09:00")
        val seedSize = 1

        val list = service.createData(BackTestingDataKey(productPriceKey, beginTime, endTime), seedSize)

        assertEquals(2, list.size)
        assertTrue(OffsetDateTime.parse("2024-05-30T15:29+09:00").isEqual(list.first().time.begin), "seedList")
        assertTrue(OffsetDateTime.parse("2024-05-31T09:00+09:00").isEqual(list.last().time.begin))
    }

    @Test
    fun `장휴일 기간이 포함된 경우`() = runTest {
        val beginTime = OffsetDateTime.parse("2024-05-31T09:00+09:00")
        val endTime = OffsetDateTime.parse("2024-06-03T09:01+09:00")
        val seedSize = 0

        val list = service.createData(BackTestingDataKey(productPriceKey, beginTime, endTime), seedSize)

        assertEquals(DayOfWeek.FRIDAY, beginTime.dayOfWeek)
        assertEquals(DayOfWeek.MONDAY, endTime.dayOfWeek)

        assertEquals(382, list.size)
        assertTrue(OffsetDateTime.parse("2024-05-31T09:00+09:00").isEqual(list.first().time.begin))
        assertTrue(OffsetDateTime.parse("2024-05-31T15:29+09:00").isEqual(list[list.size - 2].time.begin))
        assertTrue(OffsetDateTime.parse("2024-06-03T09:00+09:00").isEqual(list[list.size - 1].time.begin))
    }

    @Test
    fun `seedSize 만 있는 경우`() = runTest {
        val beginTime = OffsetDateTime.parse("2024-05-31T08:59+09:00")
        val endTime = OffsetDateTime.parse("2024-05-31T08:59+09:00")
        val seedSize = 400

        val list = service.createData(BackTestingDataKey(productPriceKey, beginTime, endTime), seedSize)

        assertEquals(400, list.size)
        assertTrue(OffsetDateTime.parse("2024-05-29T15:02+09:00").isEqual(list.first().time.begin))
        assertTrue(OffsetDateTime.parse("2024-05-30T15:29+09:00").isEqual(list.last().time.begin))
    }
}

@DisplayName("바이빗 백테스팅 데이터 조회 테스트")
class ByBitCreateBackTestingDataServiceTest {
    private val service = CreateBackTestingDataService(loadProductPriceProxy())
    private val productPriceKey = ProductPriceKey(
        Market.BY_BIT,
        ProductType.SPOT,
        "BTCUSDT",
        Duration.ofMinutes(1),
    )

    @Test
    fun `조회 시작시간과 종료시간이 같은 경우`() = runTest {
        val beginTime = OffsetDateTime.parse("2024-05-31T09:00+09:00")
        val endTime = OffsetDateTime.parse("2024-05-31T09:00+09:00")
        val seedSize = 0

        val list = service.createData(BackTestingDataKey(productPriceKey, beginTime, endTime), seedSize)

        assertEquals(emptyList(), list)
    }

    @Test
    fun `1개 데이터 조회`() = runTest {
        val beginTime = OffsetDateTime.parse("2024-05-31T09:00+09:00")
        val endTime = OffsetDateTime.parse("2024-05-31T09:01+09:00")
        val seedSize = 0

        val list = service.createData(BackTestingDataKey(productPriceKey, beginTime, endTime), seedSize)

        assertEquals(1, list.size)
        assertTrue(OffsetDateTime.parse("2024-05-31T09:00+09:00").isEqual(list.first().time.begin))
    }

    @Test
    fun `seedSize 가 있는 경우`() = runTest {
        val beginTime = OffsetDateTime.parse("2024-05-31T09:00+09:00")
        val endTime = OffsetDateTime.parse("2024-05-31T09:01+09:00")
        val seedSize = 1

        val list = service.createData(BackTestingDataKey(productPriceKey, beginTime, endTime), seedSize)

        assertEquals(2, list.size)
        assertTrue(OffsetDateTime.parse("2024-05-31T08:59+09:00").isEqual(list.first().time.begin), "seedList")
        assertTrue(OffsetDateTime.parse("2024-05-31T09:00+09:00").isEqual(list.last().time.begin))
    }

    @Test
    fun `seedSize 만 있는 경우`() = runTest {
        val beginTime = OffsetDateTime.parse("2024-05-31T08:59+09:00")
        val endTime = OffsetDateTime.parse("2024-05-31T08:59+09:00")
        val seedSize = 400

        val list = service.createData(BackTestingDataKey(productPriceKey, beginTime, endTime), seedSize)

        assertEquals(400, list.size)
        assertTrue(
            OffsetDateTime.parse("2024-05-31T02:19+09:00").isEqual(list.first().time.begin),
            "장마감 시간이 없기 때문에 국내 주식시장과 시간이 다름"
        )
        assertTrue(
            OffsetDateTime.parse("2024-05-31T08:58+09:00").isEqual(list.last().time.begin),
            "장마감 시간이 없기 때문에 국내 주식시장과 시간이 다름"
        )
    }
}