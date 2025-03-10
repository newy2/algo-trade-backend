package com.newy.algotrade.unit.product.service

import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.common.consts.ProductType
import com.newy.algotrade.product.domain.RegisterProduct
import com.newy.algotrade.product.domain.RegisterProducts
import com.newy.algotrade.product.port.out.DeleteProductsOutPort
import com.newy.algotrade.product.port.out.FindAllProductsOutPort
import com.newy.algotrade.product.port.out.SaveProductsOutPort
import com.newy.algotrade.product.service.RegisterProductsCommandService
import helpers.spring.MethodAnnotationTestHelper
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@DisplayName("DB에 저장된 상품이 없는 경우")
class EmptySavedRegisterProductCommandServiceTest : BaseRegisterProductCommandServiceTest() {
    private val service = newService(
        findAllProductsOutPort = { RegisterProducts(emptyList()) }
    )

    @Test
    fun `외부 시스템 상품 리스트가 없는 경우, DB에 데이터가 저장되지 않는다`() = runTest {
        val externalSystemProducts = emptyList<RegisterProduct>()
        val result = service.setProducts(
            fetchedProducts = RegisterProducts(externalSystemProducts)
        )

        assertEquals(0, result.savedCount)
        assertEquals(0, result.deletedCount)
    }

    @Test
    fun `외부 시스템 상품 리스트가 있는 경우, DB에 데이터가 저장된다`() = runTest {
        val externalSystemProducts = listOf(createByBitProduct("BTC"))
        val result = service.setProducts(
            fetchedProducts = RegisterProducts(externalSystemProducts)
        )

        assertEquals(1, result.savedCount)
        assertEquals(0, result.deletedCount)
    }
}

@DisplayName("DB에 저장된 상품이 있는 경우")
class AlreadySavedRegisterProductCommandServiceTest : BaseRegisterProductCommandServiceTest() {
    private val savedProduct = createByBitProduct("BTC")
    private val service = newService(
        findAllProductsOutPort = { RegisterProducts(listOf(savedProduct)) }
    )

    @Test
    fun `외부 시스템 상품 리스트가 DB에 저장된 상품 리스트보다 적은 경우, DB의 데이터가 삭제된다`() = runTest {
        val externalSystemProducts = emptyList<RegisterProduct>()
        val result = service.setProducts(
            fetchedProducts = RegisterProducts(externalSystemProducts)
        )

        assertEquals(0, result.savedCount)
        assertEquals(1, result.deletedCount)
    }

    @Test
    fun `외부 시스템 상품 리스트와 DB에 저장된 상품 리스트가 같은 경우, DB의 데이터는 변화가 없다`() = runTest {
        val externalSystemProducts = listOf(savedProduct)
        val result = service.setProducts(
            fetchedProducts = RegisterProducts(externalSystemProducts)
        )

        assertEquals(0, result.savedCount)
        assertEquals(0, result.deletedCount)
    }

    @Test
    fun `외부 시스템 상품 리스트가 DB에 저장된 상품 리스트보다 많은 경우, DB의 데이터가 추가된다`() = runTest {
        val externalSystemProducts = listOf(savedProduct, createByBitProduct("ETH"))
        val result = service.setProducts(
            fetchedProducts = RegisterProducts(externalSystemProducts)
        )

        assertEquals(1, result.savedCount)
        assertEquals(0, result.deletedCount)
    }

    @Test
    fun `외부 시스템 상품 리스트가 DB에 저장된 상품 리스트와 다른 경우, DB의 데이터가 삭제되고 추가된다`() = runTest {
        val externalSystemProducts = listOf(createByBitProduct("ETH"), createByBitProduct("SOL"))
        val result = service.setProducts(
            fetchedProducts = RegisterProducts(externalSystemProducts)
        )

        assertEquals(2, result.savedCount)
        assertEquals(1, result.deletedCount)
    }
}

class RegisterProductCommandServiceAnnotationTest {
    @Test
    fun `메서드 애너테이션 사용 여부 확인`() {
        assertTrue(MethodAnnotationTestHelper(RegisterProductsCommandService::setProducts).hasWritableTransactionalAnnotation())
    }
}

open class BaseRegisterProductCommandServiceTest() {
    protected fun newService(
        findAllProductsOutPort: FindAllProductsOutPort = FindAllProductsOutPort { RegisterProducts(products = emptyList()) },
        saveProductsOutPort: SaveProductsOutPort = SaveProductsOutPort { it.products.size },
        deleteProductsOutPort: DeleteProductsOutPort = DeleteProductsOutPort { it.products.size },
    ) = RegisterProductsCommandService(
        findAllProductsOutPort = findAllProductsOutPort,
        saveProductsOutPort = saveProductsOutPort,
        deleteProductsOutPort = deleteProductsOutPort,
    )

    protected fun createByBitProduct(code: String): RegisterProduct {
        return RegisterProduct(
            marketCode = MarketCode.BY_BIT,
            type = ProductType.SPOT,
            code = code,
            name = code
        )
    }
}