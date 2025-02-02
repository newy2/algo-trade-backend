package com.newy.algotrade.unit.market_account.adapter.out.external_system.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.newy.algotrade.common.mapper.JsonConverterByJackson
import com.newy.algotrade.common.mapper.toObject
import com.newy.algotrade.market_account.adapter.out.external_system.model.LsSecGetWalletBalanceHttpResponse
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LsSecGetWalletBalanceHttpResponseTest {
    private val converter = JsonConverterByJackson(jacksonObjectMapper())

    @Test
    fun `요청이 성공한 경우 rsp_cd 값은 0 이다`() {
        val rawResponse = """
        {
          "rsp_cd" : "00000",
          "rsp_msg" : "조회가 완료되었습니다.",
          "t0424OutBlock" : {},
          "t0424OutBlock1" : []
        }
        """

        val response = converter.toObject<LsSecGetWalletBalanceHttpResponse>(rawResponse)

        assertTrue(response.isSuccess())
    }

    @Test
    fun `요청이 실패한 경우 rsp_cd 값이 0 이 아니다`() {
        val rawResponse = """
        {
          "rsp_cd" : "00001",
          "rsp_msg" : "알 수 없는 에러가 발생했습니다.",
          "t0424OutBlock" : {},
          "t0424OutBlock1" : []
        }
        """

        val response = converter.toObject<LsSecGetWalletBalanceHttpResponse>(rawResponse)

        assertFalse(response.isSuccess())
    }
}