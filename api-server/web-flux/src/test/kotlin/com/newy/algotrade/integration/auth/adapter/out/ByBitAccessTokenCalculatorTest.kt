package com.newy.algotrade.integration.auth.adapter.out

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.auth.adpter.out.internal_system.ByBitAccessTokenCalculator
import com.newy.algotrade.auth.domain.ByBitPrivateApiInfo
import com.newy.algotrade.auth.domain.PrivateApiInfo
import com.newy.algotrade.common.mapper.JsonConverterByJackson
import com.newy.algotrade.common.web.default_implement.DefaultHttpApiClient
import com.newy.algotrade.common.web.http.get
import helpers.BaseDisabledTest
import helpers.TestEnv
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledIf
import java.time.Instant

// TODO 재작성 필요
class ByBitPrivateApiTest : BaseDisabledTest {
    @DisabledIf("hasNotByBitApiInfo")
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
                privateApiInfo = PrivateApiInfo(
                    appKey = TestEnv.ByBit.apiKey,
                    appSecret = TestEnv.ByBit.apiSecret,
                ),
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