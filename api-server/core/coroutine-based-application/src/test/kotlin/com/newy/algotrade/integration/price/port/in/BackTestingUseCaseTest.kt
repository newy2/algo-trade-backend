package com.newy.algotrade.integration.price.port.`in`

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.coroutine_based_application.auth.adpter.out.web.EBestAccessTokenHttpApi
import com.newy.algotrade.coroutine_based_application.common.web.default_implement.DefaultHttpApiClient
import com.newy.algotrade.coroutine_based_application.price.domain.ProductPriceProvider
import com.newy.algotrade.coroutine_based_application.price.domain.UserStrategyRunner
import com.newy.algotrade.coroutine_based_application.price2.adpter.out.web.FetchByBitProductPrice
import com.newy.algotrade.coroutine_based_application.price2.adpter.out.web.FetchEBestProductPrice
import com.newy.algotrade.coroutine_based_application.price2.adpter.out.web.GetProductPriceProxy
import com.newy.algotrade.coroutine_based_application.price2.domain.back_test.BackTestDataLoader
import com.newy.algotrade.coroutine_based_application.price2.domain.back_test.StringReporter
import com.newy.algotrade.domain.auth.adapter.out.common.model.PrivateApiInfo
import com.newy.algotrade.domain.chart.DEFAULT_CHART_FACTORY
import com.newy.algotrade.domain.chart.strategy.custom.BuyTripleRSIStrategy
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.common.mapper.JsonConverterByJackson
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import helpers.TestEnv
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.test.assertEquals

private fun eBestHttpApiClient() =
    DefaultHttpApiClient(
        OkHttpClient(),
        TestEnv.EBest.url, // TODO 프로덕션 서버 데이터 사용하기
        JsonConverterByJackson(jacksonObjectMapper())
    )

private fun byBitHttpApiClient() =
    DefaultHttpApiClient(
        OkHttpClient(),
        "https://api.bybit.com", // 백테스팅은 프로덕션 서버 데이터 사용
        JsonConverterByJackson(jacksonObjectMapper())
    )

private fun loadProductPriceProxy() =
    GetProductPriceProxy(
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

class BackTestingUseCaseTest {
    @Test
    fun `트리플 RSI 백테스팅`() = runBlocking {
        val dataLoader = BackTestDataLoader(
            loadProductPriceProxy(),
            OffsetDateTime.parse("2023-06-01T00:00Z"),
            OffsetDateTime.parse("2023-06-06T00:25Z"),
            coroutineContext,
        )

        val provider = ProductPriceProvider(dataLoader, dataLoader)

        val candles = DEFAULT_CHART_FACTORY.candles()
        val userStrategyRunner = UserStrategyRunner(
            ProductPriceProvider.Key(
                "test1",
                ProductPriceKey(
                    Market.BY_BIT,
                    ProductType.SPOT,
                    "BTCUSDT",
                    Duration.ofMinutes(5),
                ),
            ),
            candles = candles,
            strategy = BuyTripleRSIStrategy(candles),
        )

        provider.putListener(userStrategyRunner.productPriceProviderKey, userStrategyRunner)

        dataLoader.awaitFinish()

        val reporter = StringReporter(userStrategyRunner.history)
        println(reporter.report())
        assertEquals(
            """
                ENTRY TIME: 2023-06-02T04:30Z
                EXIT TIME: 2023-06-05T15:50Z
                ENTRY PRICE: 27018.14
                EXIT PRICE: 26298.52
                --------------------
                TOTAL REVENUE: -719.62
                TOTAL REVENUE RATE: -3.00%
                TOTAL TRANSACTION COUNT: 1 (ENTRY: 1, EXIT: 1)
            """.trimIndent(),
            reporter.report()
        )
    }

    @Test
    fun `캐시 데이터 생성`() = runBlocking {
        val tester = BackTestDataLoader(
            loadProductPriceProxy(),
            OffsetDateTime.parse("2023-06-01T00:00+09:00"),
            OffsetDateTime.parse("2024-05-01T00:00+09:00"),
            coroutineContext,
        )

        listOf(
            Triple(Duration.ofMinutes(1), "2023-06-01T00:00+09:00", "2024-05-01T00:00+09:00"),
            Triple(Duration.ofMinutes(3), "2023-06-01T00:00+09:00", "2024-05-01T00:00+09:00"),
            Triple(Duration.ofMinutes(5), "2023-06-01T00:00+09:00", "2024-05-01T00:00+09:00"),
            Triple(Duration.ofMinutes(15), "2023-06-01T00:00+09:00", "2024-05-01T00:00+09:00"),
            Triple(Duration.ofMinutes(30), "2023-06-01T00:00+09:00", "2024-05-01T00:00+09:00"),
            Triple(Duration.ofHours(1), "2023-06-01T00:00+09:00", "2024-05-01T00:00+09:00"),
            Triple(Duration.ofDays(1), "2023-06-01T00:00+09:00", "2024-05-01T00:00+09:00"),
        ).forEach { (interval, start, end) ->
            tester.loadProductPrices(
                BackTestDataLoader.Key(
                    ProductPriceKey(
                        Market.BY_BIT,
                        ProductType.SPOT,
                        "BTCUSDT",
                        interval
                    ),
                    OffsetDateTime.parse(start),
                    OffsetDateTime.parse(end),
                )
            )

            tester.loadProductPrices(
                BackTestDataLoader.Key(
                    ProductPriceKey(
                        Market.E_BEST,
                        ProductType.SPOT,
                        "005930",
                        interval
                    ),
                    OffsetDateTime.parse(start),
                    OffsetDateTime.parse(end),
                )
            )

            ProductPriceKey(
                Market.E_BEST,
                ProductType.SPOT,
                "005930",
                Duration.ofMinutes(1)
            )
            println("[FIN] $start ~ $end")
        }
    }
}