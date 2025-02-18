package com.newy.algotrade.unit.product.domain

import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.common.consts.ProductType
import com.newy.algotrade.product.domain.RegisterProduct
import com.newy.algotrade.product.domain.RegisterProducts
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@DisplayName("DB에 저장된 상품이 없는 경우")
class EmptySavedRegisterProductTest : BaseRegisterProductTest() {
    @Test
    fun `처음 상품 등록하기`() {
        val savedProducts = RegisterProducts(emptyList())
        val fetchedProducts = RegisterProducts(listOf(createByBitProduct("BTC")))
        val savableProducts = fetchedProducts.subtract(savedProducts)

        assertEquals(RegisterProducts(listOf(createByBitProduct("BTC"))), savableProducts)
    }
}

@DisplayName("DB에 저장된 상품이 있는 경우")
class AlreadySavedRegisterProductTest : BaseRegisterProductTest() {
    private val savedProduct = createByBitProduct("BTC")
    private val savedProducts = RegisterProducts(listOf(savedProduct))

    @Test
    fun `같은 상품 등록하기`() {
        val fetchedProducts = RegisterProducts(listOf(savedProduct))
        val savableProducts = fetchedProducts.subtract(savedProducts)

        assertEquals(RegisterProducts(emptyList()), savableProducts)
    }

    @Test
    fun `다른 상품 등록하기`() {
        val fetchedProducts = RegisterProducts(listOf(createByBitProduct("ETH")))
        val savableProducts = fetchedProducts.subtract(savedProducts)

        assertEquals(RegisterProducts(listOf(createByBitProduct("ETH"))), savableProducts)
    }

    @Test
    fun `같은 상품 + 다른 상품 등록하기`() {
        val fetchedProducts = RegisterProducts(listOf(savedProduct, createByBitProduct("ETH")))
        val savableProducts = fetchedProducts.subtract(savedProducts)

        assertEquals(RegisterProducts(listOf(createByBitProduct("ETH"))), savableProducts)
    }
}

open class BaseRegisterProductTest {
    protected fun createByBitProduct(code: String): RegisterProduct {
        return RegisterProduct(
            marketCode = MarketCode.BY_BIT,
            type = ProductType.SPOT,
            code = code,
            name = code
        )
    }
}