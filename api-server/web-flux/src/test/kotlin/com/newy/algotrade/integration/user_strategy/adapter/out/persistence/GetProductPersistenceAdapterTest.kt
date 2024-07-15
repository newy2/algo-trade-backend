package com.newy.algotrade.integration.user_strategy.adapter.out.persistence

import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.GetProductPersistenceAdapter
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.repository.MarketRepositoryForStrategy
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.repository.ProductRepository
import helpers.BaseDbTest
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.properties.Delegates

class GetProductPersistenceAdapterTest(
    @Autowired private val marketRepository: MarketRepositoryForStrategy,
    @Autowired private val productRepository: ProductRepository,
    @Autowired private val adapter: GetProductPersistenceAdapter
) : BaseDbTest() {
    private var marketId by Delegates.notNull<Long>()

    @BeforeEach
    fun setUp(): Unit = runBlocking {
        marketId = marketRepository.findByCode("BY_BIT")!!.parentMarketId
    }

    @Test
    fun `DB 에 입력된 데이터 확인`() = runBlocking {
        val products = productRepository.findAll().toList()
        assertTrue(products.any { it.marketId == marketId && it.type == "SPOT" && it.code == "BTC" })
        assertTrue(products.any { it.marketId == marketId && it.type == "SPOT" && it.code == "ETH" })
        assertFalse(products.any { it.marketId == marketId && it.type == "SPOT" && it.code == "XXX" })
    }

    @Test
    fun `저장된 상품코드 조회하기`() = runBlocking {
        assertEquals(
            listOf("BTC", "ETH"),
            adapter.getProducts(
                marketIds = listOf(marketId),
                productType = ProductType.SPOT,
                productCodes = listOf("BTC", "ETH")
            ).map { it.code }
        )
    }

    @Test
    fun `저장되지 않은 상품코드 조회하기`() = runBlocking {
        assertEquals(
            emptyList<String>(),
            adapter.getProducts(
                marketIds = listOf(marketId),
                productType = ProductType.SPOT,
                productCodes = listOf("XXX")
            ).map { it.code }
        )
    }

    @Test
    fun `저장된 상품코드와 저장되지 않은 상품코드 함께 조회하기`() = runBlocking {
        assertEquals(
            listOf("BTC"),
            adapter.getProducts(
                marketIds = listOf(marketId),
                productType = ProductType.SPOT,
                productCodes = listOf("BTC", "XXX")
            ).map { it.code }
        )
    }
}
