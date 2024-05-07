package com.newy.algotrade.integration.libs.helper

import helpers.TestEnv
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TestEnvTest {
    @Test
    fun `테스트 환경변수 확인`() {
        TestEnv.ByBit.let {
            assertEquals("https://api-testnet.bybit.com", it.url)
            assertTrue(it.apiKey.isNotEmpty())
            assertTrue(it.apiSecret.isNotEmpty())
        }

        TestEnv.EBest.let {
            assertEquals("https://openapi.ebestsec.co.kr:8080", it.url)
            assertTrue(it.apiKey.isNotEmpty())
            assertTrue(it.apiSecret.isNotEmpty())
        }
    }
}