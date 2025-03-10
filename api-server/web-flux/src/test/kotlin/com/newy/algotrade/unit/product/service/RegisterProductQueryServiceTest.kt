package com.newy.algotrade.unit.product.service

import com.newy.algotrade.auth.domain.PrivateApiInfo
import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.common.consts.ProductType
import com.newy.algotrade.product.domain.RegisterProduct
import com.newy.algotrade.product.domain.RegisterProducts
import com.newy.algotrade.product.port.out.FetchProductsOutPort
import com.newy.algotrade.product.port.out.FindPrivateApiInfoOutPort
import com.newy.algotrade.product.service.RegisterProductQueryService
import helpers.spring.MethodAnnotationTestHelper
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class RegisterProductQueryServiceTest {
    private val userId = 1L

    @Test
    fun `getProducts 는 LS증권 현물 상품을 조회할 수 있다`() = runTest {
        val service = newService(
            fetchProductsOutPort = { marketCode, productType, _ ->
                if (marketCode == MarketCode.LS_SEC && productType == ProductType.SPOT) {
                    RegisterProducts(listOf(createLsSecProduct("삼성전자")))
                } else {
                    RegisterProducts(emptyList())
                }
            }
        )

        assertEquals(
            RegisterProducts(listOf(createLsSecProduct("삼성전자"))),
            service.getProducts(userId)
        )
    }

    @Test
    fun `getProducts 는 바이빗 현물 상품을 조회할 수 있다`() = runTest {
        val service = newService(
            fetchProductsOutPort = { marketCode, productType, _ ->
                if (marketCode == MarketCode.BY_BIT && productType == ProductType.SPOT) {
                    RegisterProducts(listOf(createByBitProduct("BTC")))
                } else {
                    RegisterProducts(emptyList())
                }
            }
        )

        assertEquals(
            RegisterProducts(listOf(createByBitProduct("BTC"))),
            service.getProducts(userId)
        )
    }

    @Test
    fun `getProducts 는 바이빗 무기한 선물 상품을 조회할 수 있다`() = runTest {
        val service = newService(
            fetchProductsOutPort = { marketCode, productType, _ ->
                if (marketCode == MarketCode.BY_BIT && productType == ProductType.PERPETUAL_FUTURE) {
                    RegisterProducts(listOf(createByBitProduct(code = "BTCUSDT", type = ProductType.PERPETUAL_FUTURE)))
                } else {
                    RegisterProducts(emptyList())
                }
            }
        )

        assertEquals(
            RegisterProducts(listOf(createByBitProduct(code = "BTCUSDT", type = ProductType.PERPETUAL_FUTURE))),
            service.getProducts(userId)
        )
    }


    @Test
    fun `LS 증권 의 APP_KEY, APP_SECRET 은 필수로 있어야 한다`() = runTest {
        val service = newService(
            findPrivateApiInfoOutPort = { emptyMap<MarketCode, PrivateApiInfo>() }
        )

        val error = assertThrows<IllegalArgumentException> {
            service.getProducts(userId)
        }
        assertEquals("LS_SEC 계정 정보를 찾을 수 없습니다.", error.message)
    }


    private fun newService(
        findPrivateApiInfoOutPort: FindPrivateApiInfoOutPort = FindPrivateApiInfoOutPort {
            mapOf<MarketCode, PrivateApiInfo>(
                MarketCode.LS_SEC to PrivateApiInfo(appKey = "APP_KEY", appSecret = "APP_SECRET")
            )
        },
        fetchProductsOutPort: FetchProductsOutPort = FetchProductsOutPort { _, _, _ -> RegisterProducts() },
    ) = RegisterProductQueryService(
        findPrivateApiInfoOutPort = findPrivateApiInfoOutPort,
        fetchProductsOutPort = fetchProductsOutPort
    )

    private fun createByBitProduct(code: String, type: ProductType = ProductType.SPOT): RegisterProduct {
        return RegisterProduct(
            marketCode = MarketCode.BY_BIT,
            type = type,
            code = code,
            name = code
        )
    }

    private fun createLsSecProduct(code: String): RegisterProduct {
        return RegisterProduct(
            marketCode = MarketCode.LS_SEC,
            type = ProductType.SPOT,
            code = code,
            name = code
        )
    }
}

class RegisterProductQueryServiceAnnotationTest {
    @Test
    fun `메서드 애너테이션 사용 여부 확인`() {
        assertTrue(MethodAnnotationTestHelper(RegisterProductQueryService::getProducts).hasNotTransactionalAnnotation())
    }
}