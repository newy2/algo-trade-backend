package com.newy.algotrade.unit.product.adapter.out.external_system.model

import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.common.consts.ProductType
import com.newy.algotrade.common.mapper.toObject
import com.newy.algotrade.product.adapter.out.external_system.model.LsProduct
import com.newy.algotrade.product.adapter.out.external_system.model.LsSecProductHttpResponse
import com.newy.algotrade.product.domain.RegisterProduct
import com.newy.algotrade.spring.config.JsonConverterConfig
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@DisplayName("LS 증권 현물 상품 조회 응답 데이터 파싱")
class LsSecProductHttpResponseParsingTest {
    private val jsonConverter = JsonConverterConfig().jsonConverter()

    @Test
    fun `상품이 없는 경우`() {
        val rawResponse = """
            {
              "t8436OutBlock": [],
              "rsp_cd": "00000",
              "rsp_msg": "정상적으로 조회가 완료되었습니다."
            }
            """
        val response = jsonConverter.toObject<LsSecProductHttpResponse>(rawResponse)

        assertEquals(emptyList(), response.products)
    }

    @Test
    fun `상품이 여러 개인 경우`() {
        val rawResponse = """
        {
          "t8436OutBlock": [
            {
              "hname": "동화약품",
              "shcode": "000020",
              "etfgubun": "0",
              "unUsedField": "1"
            },
            {
              "hname": "TIGER 엔비디아미국채",
              "shcode": "0000D0",
              "etfgubun": "1",
              "unUsedField": "1"
            }
          ],
          "rsp_cd": "00000",
          "rsp_msg": "정상적으로 조회가 완료되었습니다."
        }
        """
        val response = jsonConverter.toObject<LsSecProductHttpResponse>(rawResponse)

        assertEquals(
            listOf(
                LsProduct(
                    name = "동화약품",
                    code = "000020",
                    etfCode = "0",
                ),
                LsProduct(
                    name = "TIGER 엔비디아미국채",
                    code = "0000D0",
                    etfCode = "1",
                )
            ),
            response.products
        )
    }
}

class LsProductTest {
    val stockProduct = LsProduct(
        name = "동화약품",
        code = "000020",
        etfCode = "0",
    )
    val etfProduct = LsProduct(
        name = "TIGER 엔비디아미국채",
        code = "0000D0",
        etfCode = "1",
    )

    @Test
    fun `현물 상품, ETF 상품 확인하기`() {
        assertTrue(stockProduct.isStock())
        assertFalse(etfProduct.isStock())
    }

    @Test
    fun `도메인 모델로 변환하기`() {
        assertEquals(
            RegisterProduct(
                marketCode = MarketCode.LS_SEC,
                type = ProductType.SPOT,
                code = "000020",
                name = "동화약품",
            ),
            stockProduct.toDomainModel()
        )

        assertThrows<IllegalArgumentException> {
            etfProduct.toDomainModel()
        }.let {
            assertEquals("지원하지 않는 유형입니다", it.message)
        }
    }
}