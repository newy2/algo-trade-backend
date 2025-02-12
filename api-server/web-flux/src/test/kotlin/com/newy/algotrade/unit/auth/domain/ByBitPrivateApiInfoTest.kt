package com.newy.algotrade.unit.auth.domain

import com.newy.algotrade.auth.domain.ByBitPrivateApiInfo
import com.newy.algotrade.auth.domain.PrivateApiInfo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@DisplayName("바이빗 Private GET API Request Header 생성 테스트")
class ByBitPrivateHttpGetMethodInfoTest {
    val queryString = "category=spot&symbol=BTCUSDT"
    val privateApiInfo = ByBitPrivateApiInfo(
        privateApiInfo = PrivateApiInfo(
            appKey = "api-key",
            appSecret = "secret",
        ),
        timestamp = 1658385579423,
        data = queryString,
        receiveWindow = 5000,
    )

    @Test
    fun `accessToken 생성하기`() {
        assertEquals(
            "57bb56b6f64b386cbc4a312af9260fbb98c1bc6c3731eac067b2a55229fb05f9",
            privateApiInfo.getAccessToken()
        )
    }

    @Test
    fun `request headers 생성하기`() {
        assertEquals(
            mapOf(
                "X-BAPI-SIGN" to "57bb56b6f64b386cbc4a312af9260fbb98c1bc6c3731eac067b2a55229fb05f9",
                "X-BAPI-API-KEY" to "api-key",
                "X-BAPI-TIMESTAMP" to "1658385579423",
                "X-BAPI-RECV-WINDOW" to "5000",
            ),
            privateApiInfo.getRequestHeaders()
        )
    }
}

@DisplayName("바이빗 Private POST API Request Header 생성 테스트")
class ByBitPrivateHttpPostMethodInfoTest {
    val rawBody = """{"category":"option"}"""
    val privateApiInfo = ByBitPrivateApiInfo(
        privateApiInfo = PrivateApiInfo(
            appKey = "api-key",
            appSecret = "secret",
        ),
        timestamp = 1658385579423,
        data = rawBody,
        receiveWindow = 5000,
    )


    @Test
    fun `accessToken 생성하기`() {
        assertEquals(
            "38f5a5ef114062a9367e3da9cd897e2e00ba86dded436045c784b22508dde08a",
            privateApiInfo.getAccessToken()
        )
    }

    @Test
    fun `request headers 생성하기`() {
        assertEquals(
            mapOf(
                "X-BAPI-SIGN" to "38f5a5ef114062a9367e3da9cd897e2e00ba86dded436045c784b22508dde08a",
                "X-BAPI-API-KEY" to "api-key",
                "X-BAPI-TIMESTAMP" to "1658385579423",
                "X-BAPI-RECV-WINDOW" to "5000",
            ),
            privateApiInfo.getRequestHeaders()
        )
    }
}