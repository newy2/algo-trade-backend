package com.newy.algotrade.integration.product.adapter.out.web

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.coroutine_based_application.auth.adpter.out.web.LsSecAccessTokenHttpApi
import com.newy.algotrade.coroutine_based_application.common.web.default_implement.DefaultHttpApiClient
import com.newy.algotrade.coroutine_based_application.product.adapter.`in`.web.CreateBackTestingDataController
import com.newy.algotrade.coroutine_based_application.product.adapter.out.persistent.FileBackTestingDataStore
import com.newy.algotrade.coroutine_based_application.product.adapter.out.web.FetchByBitProductPrice
import com.newy.algotrade.coroutine_based_application.product.adapter.out.web.FetchLsSecProductPrice
import com.newy.algotrade.coroutine_based_application.product.adapter.out.web.FetchProductPriceProxy
import com.newy.algotrade.coroutine_based_application.product.application.service.CreateBackTestingDataService
import com.newy.algotrade.coroutine_based_application.product.application.service.SetBackTestingDataService
import com.newy.algotrade.coroutine_based_application.product.domain.BackTestingFileManager
import com.newy.algotrade.coroutine_based_application.product.port.`in`.model.BackTestingDataKey
import com.newy.algotrade.domain.auth.adapter.out.common.model.PrivateApiInfo
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.common.mapper.JsonConverterByJackson
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import helpers.TestEnv
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
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
    FetchProductPriceProxy(
        mapOf(
            Market.LS_SEC to FetchLsSecProductPrice(
                lsSecHttpApiClient(),
                LsSecAccessTokenHttpApi(lsSecHttpApiClient()),
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

class CreateRealDataBackTestingData {
    @Test
    fun `백테스팅 데이터 생성하기`() = runTest {
        val createBackTestingDataUseCase = CreateBackTestingDataService(loadProductPriceProxy())
        val setBackTestingDataUseCase = SetBackTestingDataService(
            FileBackTestingDataStore(BackTestingFileManager())
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