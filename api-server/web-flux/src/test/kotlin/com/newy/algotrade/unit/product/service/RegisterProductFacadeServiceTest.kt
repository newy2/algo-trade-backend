package com.newy.algotrade.unit.product.service

import com.newy.algotrade.auth.domain.PrivateApiInfo
import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.common.consts.ProductType
import com.newy.algotrade.product.domain.RegisterProduct
import com.newy.algotrade.product.domain.RegisterProducts
import com.newy.algotrade.product.port.`in`.model.RegisterProductResult
import com.newy.algotrade.product.port.out.*
import com.newy.algotrade.product.service.RegisterProductCommandService
import com.newy.algotrade.product.service.RegisterProductQueryService
import com.newy.algotrade.product.service.RegisterProductsFacadeService
import helpers.spring.MethodAnnotationTestHelper
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@DisplayName("DB에 저장된 상품이 없는 경우")
class RegisterProductFacadeServiceTest : BaseRegisterProductFacadeServiceTest() {
    @Test
    fun `LS 증권 의 APP_KEY, APP_SECRET 은 필수로 있어야 한다`() = runTest {
        val service = createFacadeService(
            queryService = createQueryService(
                findPrivateApiInfoOutPort = { emptyMap<MarketCode, PrivateApiInfo>() }
            )
        )
        val error = assertThrows<IllegalArgumentException> {
            service.registerProducts(userId)
        }

        assertEquals("LS_SEC 계정 정보를 찾을 수 없습니다.", error.message)
    }

    @Test
    fun `외부 시스템에서 product 를 조회할 수 없는 경우`() = runTest {
        val emptyExternalSystemProductsAdapter = FetchProductsOutPort { _, _, _ ->
            RegisterProducts(emptyList())
        }
        val service = createFacadeService(
            queryService = createQueryService(
                fetchProductsOutPort = emptyExternalSystemProductsAdapter
            )
        )
        val result = service.registerProducts(userId)

        assertEquals(RegisterProductResult(savedCount = 0, deletedCount = 0), result)
    }

    @Test
    fun `프로젝트에서 지원하는 외부 시스템 상품은 아래와 같다`() = runTest {
        val availableExternalSystemProductsAdapter = FetchProductsOutPort { marketCode, productType, _ ->
            if (marketCode == MarketCode.LS_SEC && productType == ProductType.SPOT) {
                // LS증권 - 현물 상품
                RegisterProducts(
                    listOf(
                        createLsSecProduct("삼성전자"),
                    )
                )
            } else if (marketCode == MarketCode.BY_BIT && productType == ProductType.SPOT) {
                // ByBit - 현물 상품
                RegisterProducts(
                    listOf(
                        createByBitProduct("BTC"),
                        createByBitProduct("ETH"),
                    )
                )
            } else if (marketCode == MarketCode.BY_BIT && productType == ProductType.PERPETUAL_FUTURE) {
                // ByBit - 무기한 선물 상품
                RegisterProducts(
                    listOf(
                        createByBitProduct(code = "BTCUSDT", type = ProductType.PERPETUAL_FUTURE),
                        createByBitProduct(code = "EHTUSDT", type = ProductType.PERPETUAL_FUTURE),
                        createByBitProduct(code = "SOLUSDT", type = ProductType.PERPETUAL_FUTURE),
                    )
                )
            } else {
                throw IllegalArgumentException("지원하지 않는 유형입니다.")
            }
        }
        val service = createFacadeService(
            queryService = createQueryService(
                fetchProductsOutPort = availableExternalSystemProductsAdapter,
            )
        )
        val result = service.registerProducts(userId)

        assertEquals(RegisterProductResult(savedCount = 6, deletedCount = 0), result)
    }
}

@DisplayName("DB에 저장된 상품이 있는 경우")
class RegisterProductFacadeServiceWithSavedProductsTest : BaseRegisterProductFacadeServiceTest() {
    private val savedProduct = createByBitProduct("BTC")
    private val commandService = createCommandService(
        findAllProductsOutPort = {
            RegisterProducts(listOf(savedProduct))
        }
    )

    @Test
    fun `외부 시스템에서 product 를 조회할 수 없으면, 기존에 DB 에 저장된 데이터가 삭제된다`() = runTest {
        val emptyExternalSystemProductsAdapter = createFetchByBitSpotProductAdapter(emptyList())
        val service = createFacadeService(
            queryService = createQueryService(
                fetchProductsOutPort = emptyExternalSystemProductsAdapter,
            ),
            commandService = commandService
        )
        val result = service.registerProducts(userId)

        assertEquals(RegisterProductResult(savedCount = 0, deletedCount = 1), result)
    }

    @Test
    fun `외부 시스템에서 조회한 product 와 DB에 저장된 product 가 동일하면, DB의 데이터는 변화가 없다`() = runTest {
        val sameExternalSystemProductsAdapter = createFetchByBitSpotProductAdapter(listOf(savedProduct))
        val service = createFacadeService(
            queryService = createQueryService(
                fetchProductsOutPort = sameExternalSystemProductsAdapter
            ),
            commandService = commandService
        )
        val result = service.registerProducts(userId)

        assertEquals(RegisterProductResult(savedCount = 0, deletedCount = 0), result)
    }

    @Test
    fun `외부 시스템에서 조회한 product 가 DB에 저장된 product 보다 많은 경우, DB의 데이터가 추가된다`() = runTest {
        val intersectionExternalSystemProductsAdapter = createFetchByBitSpotProductAdapter(
            listOf(savedProduct, createByBitProduct("ETH"))
        )
        val service = createFacadeService(
            queryService = createQueryService(
                fetchProductsOutPort = intersectionExternalSystemProductsAdapter
            ),
            commandService = commandService
        )
        val result = service.registerProducts(userId)

        assertEquals(RegisterProductResult(savedCount = 1, deletedCount = 0), result)
    }

    @Test
    fun `외부 시스템에서 조회한 product 가 DB에 저장된 product 와 다른 경우, 기존에 DB 에 저장된 데이터가 삭제되고 신규 데이터가 추가된다`() = runTest {
        val differentExternalSystemProductsAdapter = createFetchByBitSpotProductAdapter(
            listOf(createByBitProduct("ETH"), createByBitProduct("SOL"))
        )
        val service = createFacadeService(
            queryService = createQueryService(
                fetchProductsOutPort = differentExternalSystemProductsAdapter
            ),
            commandService = commandService
        )
        val result = service.registerProducts(userId)

        assertEquals(RegisterProductResult(savedCount = 2, deletedCount = 1), result)
    }

    private fun createFetchByBitSpotProductAdapter(products: List<RegisterProduct>) =
        FetchProductsOutPort { marketCode, productType, _ ->
            if (marketCode == MarketCode.BY_BIT && productType == ProductType.SPOT) {
                RegisterProducts(products)
            } else {
                RegisterProducts(emptyList())
            }
        }
}

open class BaseRegisterProductFacadeServiceTest {
    protected val userId = 1L

    protected fun createFacadeService(
        queryService: RegisterProductQueryService = createQueryService(),
        commandService: RegisterProductCommandService = createCommandService(),
    ) = RegisterProductsFacadeService(
        queryService,
        commandService,
    )

    protected fun createQueryService(
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

    protected fun createCommandService(
        findAllProductsOutPort: FindAllProductsOutPort = FindAllProductsOutPort { RegisterProducts(products = emptyList()) },
        saveProductsOutPort: SaveProductsOutPort = SaveProductsOutPort { it.products.size },
        deleteProductsOutPort: DeleteProductsOutPort = DeleteProductsOutPort { it.products.size },
    ) = RegisterProductCommandService(
        findAllProductsOutPort = findAllProductsOutPort,
        saveProductsOutPort = saveProductsOutPort,
        deleteProductsOutPort = deleteProductsOutPort,
    )

    protected fun createByBitProduct(code: String, type: ProductType = ProductType.SPOT): RegisterProduct {
        return RegisterProduct(
            marketCode = MarketCode.BY_BIT,
            type = type,
            code = code,
            name = code
        )
    }

    protected fun createLsSecProduct(code: String): RegisterProduct {
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
        assertTrue(MethodAnnotationTestHelper(RegisterProductsFacadeService::registerProducts).hasNotTransactionalAnnotation())

        assertTrue(MethodAnnotationTestHelper(RegisterProductQueryService::getProducts).hasNotTransactionalAnnotation())
        assertTrue(MethodAnnotationTestHelper(RegisterProductCommandService::setProducts).hasWritableTransactionalAnnotation())
    }
}