package com.newy.algotrade.unit.libs.helper

import helpers.TestEnv
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TestEnvTest {
    @Test
    fun `테스트 환경변수 확인`() {
        assertEquals("https://api-testnet.bybit.com", TestEnv.ByBit.url)
        assertEquals("https://openapivts.koreainvestment.com:29443", TestEnv.KIS.url)

        assertTrue(TestEnv.ByBit.apiKey.isNotEmpty())
        assertTrue(TestEnv.ByBit.apiSecret.isNotEmpty())
        assertTrue(TestEnv.KIS.apiKey.isNotEmpty())
        assertTrue(TestEnv.KIS.apiSecret.isNotEmpty())
    }
}