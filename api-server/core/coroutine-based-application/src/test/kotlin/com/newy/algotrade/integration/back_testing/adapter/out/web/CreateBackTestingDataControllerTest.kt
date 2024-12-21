package com.newy.algotrade.integration.back_testing.adapter.out.web

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.auth.domain.PrivateApiInfo
import com.newy.algotrade.back_testing.adapter.`in`.web.CreateBackTestingDataController
import com.newy.algotrade.back_testing.adapter.out.persistence.BackTestingDataFileStorageAdapter
import com.newy.algotrade.back_testing.domain.BackTestingDataKey
import com.newy.algotrade.back_testing.domain.BackTestingFileManager
import com.newy.algotrade.back_testing.service.CreateBackTestingDataService
import com.newy.algotrade.back_testing.service.SetBackTestingDataService
import com.newy.algotrade.common.domain.consts.Market
import com.newy.algotrade.common.domain.consts.ProductType
import com.newy.algotrade.common.domain.mapper.JsonConverterByJackson
import com.newy.algotrade.common.web.default_implement.DefaultHttpApiClient
import com.newy.algotrade.product_price.adapter.out.external_system.FetchByBitProductPrice
import com.newy.algotrade.product_price.adapter.out.external_system.FetchLsSecProductPrice
import com.newy.algotrade.product_price.adapter.out.external_system.FetchProductPriceProxyAdapter
import com.newy.algotrade.product_price.domain.ProductPriceKey
import helpers.BaseDisabledTest
import helpers.TestEnv
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledIf
import java.time.Duration
import java.time.OffsetDateTime

private fun lsSecHttpApiClient() =
    DefaultHttpApiClient(
        OkHttpClient(),
        TestEnv.LsSec.url, // TODO 프로덕션 서버 데이터 사용하기
        JsonConverterByJackson(jacksonObjectMapper())
    )

private fun byBitHttpApiClient() =
    DefaultHttpApiClient(
        OkHttpClient(),
        "https://api.bybit.com", // 백테스팅은 프로덕션 서버 데이터 사용
        JsonConverterByJackson(jacksonObjectMapper())
    )

private fun loadProductPriceProxy() =
    FetchProductPriceProxyAdapter(
        mapOf(
            Market.LS_SEC to FetchLsSecProductPrice(
                lsSecHttpApiClient(),
                com.newy.algotrade.auth.adpter.out.external_system.LsSecAccessTokenHttpApi(lsSecHttpApiClient()),
                PrivateApiInfo(
                    key = TestEnv.LsSec.apiKey,
                    secret = TestEnv.LsSec.apiSecret,
                )
            ),
            Market.BY_BIT to FetchByBitProductPrice(
                byBitHttpApiClient()
            )
        )
    )

class CreateRealDataBackTestingData : BaseDisabledTest {
    @DisabledIf("hasNotLsSecApiInfo")
    @Test
    fun `백테스팅 데이터 생성하기`() = runTest {
        val createBackTestingDataUseCase = CreateBackTestingDataService(loadProductPriceProxy())
        val setBackTestingDataUseCase = SetBackTestingDataService(
            BackTestingDataFileStorageAdapter(BackTestingFileManager())
        )

        val controller = CreateBackTestingDataController(
            createBackTestingDataUseCase,
            setBackTestingDataUseCase,
        )

        val backTestingDataKey = BackTestingDataKey(
            ProductPriceKey(
                Market.BY_BIT,
                ProductType.SPOT,
                "BTCUSDT",
                Duration.ofMinutes(1),
            ),
            OffsetDateTime.parse("2024-06-01T00:00+09:00"),
            OffsetDateTime.parse("2024-06-05T00:00+09:00"),
        )

        val isSaved = controller.createBackTestingData(backTestingDataKey)
        Assertions.assertTrue(isSaved)
    }
}