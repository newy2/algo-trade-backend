package com.newy.algotrade.integration.auth.adapter.out

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.coroutine_based_application.auth.adpter.out.local.ByBitAccessTokenCalculator
import com.newy.algotrade.coroutine_based_application.auth.adpter.out.local.model.ByBitPrivateApiInfo
import com.newy.algotrade.coroutine_based_application.common.web.get
import com.newy.algotrade.domain.common.mapper.JsonConverterByJackson
import helpers.HttpApiClientByOkHttp
import helpers.TestEnv
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals

class ByBitPrivateApiSignatureCalculatorTest {
    private val calculator = ByBitAccessTokenCalculator()

    @Test
    fun `ByBit GET API 시그니처 생성`() = runBlocking {
        assertEquals(
            "57bb56b6f64b386cbc4a312af9260fbb98c1bc6c3731eac067b2a55229fb05f9",
            calculator.accessToken(
                ByBitPrivateApiInfo(
                    key = "api-key",
                    secret = "secret",
                    timestamp = 1658385579423,
                    data = "category=spot&symbol=BTCUSDT",
                    receiveWindow = 5000,
                )
            )
        )
    }

    @Test
    fun `ByBit POST API 시그니처 생성`() = runBlocking {
        assertEquals(
            "38f5a5ef114062a9367e3da9cd897e2e00ba86dded436045c784b22508dde08a",
            calculator.accessToken(
                ByBitPrivateApiInfo(
                    key = "api-key",
                    secret = "secret",
                    timestamp = 1658385579423,
                    data = """{"category":"option"}""",
                    receiveWindow = 5000,
                )
            )
        )
    }
}

// TODO 재작성 필요
class ByBitPrivateApiTest {
    @Test
    fun test() = runBlocking {
        val client = HttpApiClientByOkHttp(
            OkHttpClient(),
            TestEnv.ByBit.url,
            JsonConverterByJackson(jacksonObjectMapper())
        )

        val now = Instant.now().toEpochMilli()
        val signature = ByBitAccessTokenCalculator().accessToken(
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