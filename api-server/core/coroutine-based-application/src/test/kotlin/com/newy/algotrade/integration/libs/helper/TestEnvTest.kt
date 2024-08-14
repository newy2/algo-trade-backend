package com.newy.algotrade.integration.libs.helper

import helpers.BaseDisabledTest
import helpers.TestEnv
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledIf

class TestEnvTest : BaseDisabledTest {
    @DisabledIf("hasNotApiInfo")
    @Test
    fun `테스트 환경변수 확인`() {
        TestEnv.ByBit.let {
            assertEquals("https://api-testnet.bybit.com", it.url)
            assertEquals("wss://stream.bybit.com/v5/public/spot", it.socketUrl)
            assertTrue(it.apiKey.isNotEmpty())
            assertTrue(it.apiSecret.isNotEmpty())
        }

        TestEnv.LsSec.let {
            assertEquals("https://openapi.ls-sec.co.kr:8080", it.url)
            assertTrue(it.apiKey.isNotEmpty())
            assertTrue(it.apiSecret.isNotEmpty())
        }
    }
}