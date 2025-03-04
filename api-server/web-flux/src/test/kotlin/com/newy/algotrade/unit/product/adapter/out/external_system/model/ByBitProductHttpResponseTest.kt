package com.newy.algotrade.unit.product.adapter.out.external_system.model

import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.common.consts.ProductType
import com.newy.algotrade.common.mapper.toObject
import com.newy.algotrade.product.adapter.out.external_system.model.ByBitProduct
import com.newy.algotrade.product.adapter.out.external_system.model.ByBitProductHttpResponse
import com.newy.algotrade.product.domain.RegisterProduct
import com.newy.algotrade.spring.config.JsonConverterConfig
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ByBitProductHttpResponseTest {
    val jsonConverter = JsonConverterConfig().jsonConverter()

    @Test
    fun `상품이 없는 경우`() {
        val rawResponse = """
            {
                "retCode": 0,
                "retMsg": "OK",
                "result": {
                    "category": "spot",
                    "list": []
                },
                "retExtInfo": {},
                "time": 1739893339918
            }
            """
        val response = jsonConverter.toObject<ByBitProductHttpResponse>(
            source = rawResponse,
            extraValues = ByBitProductHttpResponse.jsonExtraValues(productType = ProductType.SPOT)
        )

        assertEquals(emptyList(), response.products)
    }

    @Test
    fun `상품이 여러 개인 경우`() {
        val rawResponse = """
        {
            "retCode": 0,
            "retMsg": "OK",
            "result": {
                "category": "spot",
                "list": [
                    {
                        "symbol": "BTCUSDT",
                        "baseCoin": "BTC",
                        "quoteCoin": "USDT"
                    },
                    {
                        "symbol": "ETHUSDT",
                        "baseCoin": "ETH",
                        "quoteCoin": "USDT"
                    }
                ]
            },
            "retExtInfo": {},
            "time": 1739893339918
        }
        """
        val response = jsonConverter.toObject<ByBitProductHttpResponse>(
            source = rawResponse,
            extraValues = ByBitProductHttpResponse.jsonExtraValues(productType = ProductType.SPOT)
        )

        assertEquals(
            listOf(
                ByBitProduct(
                    productType = ProductType.SPOT,
                    name = "BTCUSDT",
                    code = "BTCUSDT",
                ),
                ByBitProduct(
                    productType = ProductType.SPOT,
                    name = "ETHUSDT",
                    code = "ETHUSDT",
                ),
            ),
            response.products
        )
    }
}

class ByBitProductTest {
    @Test
    fun `도메인 모델로 변환하기`() {
        assertEquals(
            RegisterProduct(
                marketCode = MarketCode.BY_BIT,
                type = ProductType.SPOT,
                code = "BTCUSDT",
                name = "BTCUSDT"
            ),
            ByBitProduct(
                productType = ProductType.SPOT,
                name = "BTCUSDT",
                code = "BTCUSDT",
            ).toDomainModel(),
        )
        assertEquals(
            RegisterProduct(
                marketCode = MarketCode.BY_BIT,
                type = ProductType.PERPETUAL_FUTURE,
                code = "ETHUSDT",
                name = "ETHUSDT"
            ),
            ByBitProduct(
                productType = ProductType.PERPETUAL_FUTURE,
                name = "ETHUSDT",
                code = "ETHUSDT",
            ).toDomainModel(),
        )
    }
}