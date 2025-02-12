package com.newy.algotrade.unit.market_account.adapter.out.external_system.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.common.mapper.JsonConverterByJackson
import com.newy.algotrade.common.mapper.toObject
import com.newy.algotrade.market_account.adapter.out.external_system.model.ByBitGetWalletBalanceHttpResponse
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ByBitGetWalletBalanceHttpResponseTest {
    private val converter = JsonConverterByJackson(jacksonObjectMapper())

    @Test
    fun `요청이 성공한 경우 retCode 값은 0 이다`() {
        val rawResponse = """
        {
          "retCode" : 0,
          "retMsg" : "OK",
          "result" : {
            "list" : []
          },
          "retExtInfo" : {},
          "time" : 1738239078368
        }
        """

        val response = converter.toObject<ByBitGetWalletBalanceHttpResponse>(rawResponse)

        assertTrue(response.isSuccess())
    }

    @Test
    fun `요청이 실패한 경우 retCode 값이 0 이 아니다`() {
        val rawResponse = """
        {
          "retCode" : 10000,
          "retMsg" : "Server Timeout",
          "result" : {
            "list" : []
          },
          "retExtInfo" : {},
          "time" : 1738239078368
        }
        """

        val response = converter.toObject<ByBitGetWalletBalanceHttpResponse>(rawResponse)

        assertFalse(response.isSuccess())
    }
}