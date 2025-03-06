package com.newy.algotrade.integration.product.out.adapter.external_system

import com.newy.algotrade.auth.domain.PrivateApiInfo
import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.common.consts.ProductType
import com.newy.algotrade.product.adapter.out.external_system.FetchProductAdapter
import com.newy.algotrade.product.domain.RegisterProduct
import helpers.BaseDisabledTest
import helpers.TestEnv
import helpers.spring.BaseDataR2dbcTest
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledIf
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FetchProductAdapterTest(
    @Autowired private val adapter: FetchProductAdapter,
) : BaseDisabledTest, BaseDataR2dbcTest() {
    @Test
    @DisabledIf("hasNotLsSecApiInfo")
    fun `LS 증권 - 현물 상품 정보 조회하기`() = runTest {
        val registerProducts = adapter.fetchProducts(
            marketCode = MarketCode.LS_SEC,
            productType = ProductType.SPOT,
            privateApiInfos = mapOf(
                MarketCode.LS_SEC to PrivateApiInfo(
                    appKey = TestEnv.LsSec.apiKey,
                    appSecret = TestEnv.LsSec.apiSecret,
                ),
            )
        )

        assertTrue(registerProducts.products.isNotEmpty())
        assertEquals(
            RegisterProduct(
                marketCode = MarketCode.LS_SEC,
                type = ProductType.SPOT,
                code = "005930",
                name = "삼성전자",
            ),
            registerProducts.products.find { it.name == "삼성전자" }
        )
        assertNull(registerProducts.products.find { it.name == "PLUS 한화그룹주" }, "EFT 상품은 제외한다")
    }

    @Test
    @DisabledIf("hasNotByBitApiInfo")
    fun `ByBit - 현물 상품 정보 조회하기`() = runTest {
        val registerProducts = adapter.fetchProducts(
            marketCode = MarketCode.BY_BIT,
            productType = ProductType.SPOT,
            privateApiInfos = mapOf(
                MarketCode.BY_BIT to PrivateApiInfo(
                    appKey = TestEnv.ByBit.apiKey,
                    appSecret = TestEnv.ByBit.apiSecret,
                ),
            )
        )

        assertTrue(registerProducts.products.isNotEmpty())
        assertEquals(
            RegisterProduct(
                marketCode = MarketCode.BY_BIT,
                type = ProductType.SPOT,
                code = "BTCUSDT",
                name = "BTCUSDT",
            ),
            registerProducts.products.find { it.name == "BTCUSDT" }
        )
    }

    @Test
    @DisabledIf("hasNotByBitApiInfo")
    fun `무기한 선물 상품 정보 조회하기`() = runTest {
        val registerProducts = adapter.fetchProducts(
            marketCode = MarketCode.BY_BIT,
            productType = ProductType.PERPETUAL_FUTURE,
            privateApiInfos = mapOf(
                MarketCode.BY_BIT to PrivateApiInfo(
                    appKey = TestEnv.ByBit.apiKey,
                    appSecret = TestEnv.ByBit.apiSecret,
                ),
            )
        )

        assertTrue(registerProducts.products.isNotEmpty())
        assertEquals(
            RegisterProduct(
                marketCode = MarketCode.BY_BIT,
                type = ProductType.PERPETUAL_FUTURE,
                code = "ETHUSDT",
                name = "ETHUSDT",
            ),
            registerProducts.products.find { it.name == "ETHUSDT" }
        )
    }
}