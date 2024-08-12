package com.newy.algotrade.integration.auth.adapter.out

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.coroutine_based_application.auth.adpter.out.internal_system.ByBitAccessTokenCalculator
import com.newy.algotrade.coroutine_based_application.common.web.default_implement.DefaultHttpApiClient
import com.newy.algotrade.coroutine_based_application.common.web.http.get
import com.newy.algotrade.domain.auth.ByBitPrivateApiInfo
import com.newy.algotrade.domain.common.mapper.JsonConverterByJackson
import helpers.TestEnv
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Instant

// TODO 재작성 필요
class ByBitPrivateApiTest {
    @Test
    fun test() = runBlocking {
        val client = DefaultHttpApiClient(
            OkHttpClient(),
            TestEnv.ByBit.url,
            JsonConverterByJackson(jacksonObjectMapper())
        )

        val now = Instant.now().toEpochMilli()
        val signature = ByBitAccessTokenCalculator().findAccessToken(
            ByBitPrivateApiInfo(
                key = TestEnv.ByBit.apiKey,
                secret = TestEnv.ByBit.apiSecret,
                timestamp = now,
                data = "category=spot",
                receiveWindow = 5000,
            )
        )

        val response = client.get<String>(
            path = "/v5/account/fee-rate",
            headers = mapOf(
                "X-BAPI-SIGN" to signature,
                "X-BAPI-API-KEY" to TestEnv.ByBit.apiKey,
                "X-BAPI-TIMESTAMP" to "$now",
                "X-BAPI-RECV-WINDOW" to "5000",
            ),
            params = mapOf(
                "category" to "spot",
            ),
        )

        Assertions.assertTrue(response.isNotEmpty())
//        Assertions.assertEquals("", response.isNotEmpty())
    }
}