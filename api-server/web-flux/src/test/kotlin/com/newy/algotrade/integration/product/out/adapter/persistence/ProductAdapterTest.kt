package com.newy.algotrade.integration.product.out.adapter.persistence

import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.common.consts.ProductType
import com.newy.algotrade.product.adapter.out.persistence.ProductAdapter
import com.newy.algotrade.product.domain.RegisterProduct
import com.newy.algotrade.product.domain.RegisterProducts
import helpers.diffSeconds
import helpers.spring.BaseDataR2dbcTest
import kotlinx.coroutines.delay
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitSingle
import java.time.LocalDateTime
import kotlin.test.assertEquals

class ProductAdapterTest(
    @Autowired private val adapter: ProductAdapter,
    @Autowired private val databaseClient: DatabaseClient,
) : BaseDataR2dbcTest() {
    @Test
    fun `없는 상품 조회하기`() = runTransactional {
        val savedProducts = adapter.findAllProducts()
        assertEquals(RegisterProducts(products = emptyList()), savedProducts)
    }

    @Test
    fun `저장된 상품 조회하기`() = runTransactional {
        val addedCount = adapter.saveProducts(
            RegisterProducts(
                products = listOf(
                    createByBitProduct(name = "BTCUSDT"),
                    createLsSecProduct(name = "삼성전자"),
                )
            )
        )

        assertEquals(2, addedCount)
        assertEquals(
            RegisterProducts(
                products = listOf(
                    createByBitProduct(name = "BTCUSDT"),
                    createLsSecProduct(name = "삼성전자"),
                )
            ),
            adapter.findAllProducts()
        )
    }

    @Test
    fun `저장된 상품 삭제하기`() = runTransactional {
        adapter.saveProducts(
            RegisterProducts(
                products = listOf(
                    createByBitProduct(name = "BTCUSDT"),
                    createLsSecProduct(name = "삼성전자"),
                )
            )
        )

        val deletedCount = adapter.deleteProducts(
            RegisterProducts(
                products = listOf(
                    createByBitProduct(name = "BTCUSDT"),
                    createLsSecProduct(name = "삼성전자"),
                )
            )
        )

        assertEquals(2, deletedCount)
        assertEquals(RegisterProducts(products = emptyList()), adapter.findAllProducts())
    }

    @Test
    fun `상품은 소프트 삭제 처리한다`() = runTransactional {
        adapter.saveProducts(
            RegisterProducts(
                products = listOf(
                    createByBitProduct(name = "BTCUSDT"),
                )
            )
        )
        delay(1000)
        adapter.deleteProducts(
            RegisterProducts(
                products = listOf(
                    createByBitProduct(name = "BTCUSDT"),
                )
            )
        )

        val savedProduct = selectProductIdByName(name = "BTCUSDT")
        assertEquals("N", savedProduct["use_yn"])
        assertEquals(
            1,
            diffSeconds(
                from = LocalDateTime.parse(savedProduct["created_at"].toString()),
                to = LocalDateTime.parse(savedProduct["updated_at"].toString())
            )
        )
    }

    private fun createByBitProduct(name: String) =
        RegisterProduct(
            marketCode = MarketCode.BY_BIT,
            type = ProductType.SPOT,
            code = name,
            name = name,
        )

    private fun createLsSecProduct(name: String) =
        RegisterProduct(
            marketCode = MarketCode.LS_SEC,
            type = ProductType.SPOT,
            code = name,
            name = name,
        )

    private suspend fun selectProductIdByName(name: String): Map<String, Any> {
        val user = databaseClient
            .sql("SELECT id, use_yn, updated_at, created_at FROM product WHERE name = :name")
            .bind("name", name)
            .fetch()
            .awaitSingle()

        return user
    }
}