package com.newy.algotrade.unit.auth

import com.newy.algotrade.domain.auth.ByBitPrivateApiInfo
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ByBitPrivateApiInfoTest {
    @Test
    fun `ByBit GET API 시그니처 생성`() {
        assertEquals(
            "57bb56b6f64b386cbc4a312af9260fbb98c1bc6c3731eac067b2a55229fb05f9",
            ByBitPrivateApiInfo(
                key = "api-key",
                secret = "secret",
                timestamp = 1658385579423,
                data = "category=spot&symbol=BTCUSDT",
                receiveWindow = 5000,
            ).accessToken()
        )
    }

    @Test
    fun `ByBit POST API 시그니처 생성`() {
        assertEquals(
            "38f5a5ef114062a9367e3da9cd897e2e00ba86dded436045c784b22508dde08a",
            ByBitPrivateApiInfo(
                key = "api-key",
                secret = "secret",
                timestamp = 1658385579423,
                data = """{"category":"option"}""",
                receiveWindow = 5000,
            ).accessToken()
        )
    }
}