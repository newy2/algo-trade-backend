package com.newy.algotrade.integration.user_strategy.adapter.out.persistence

import com.newy.algotrade.common.domain.consts.ProductType
import com.newy.algotrade.user_strategy.adapter.out.persistence.FindProductPersistenceAdapter
import com.newy.algotrade.user_strategy.adapter.out.persistence.repository.MarketRepositoryForStrategy
import com.newy.algotrade.user_strategy.adapter.out.persistence.repository.ProductRepository
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
    @Autowired private val adapter: FindProductPersistenceAdapter
) : BaseDbTest() {
    private var marketId by Delegates.notNull<Long>()

    @BeforeEach
    fun setUp(): Unit = runBlocking {
        marketId = marketRepository.findByCode("BY_BIT")!!.id
    }

    @Test
    fun `DB 에 입력된 데이터 확인`() = runBlocking {
        val products = productRepository.findAll().toList()
        assertTrue(products.any { it.marketId == marketId && it.type == "SPOT" && it.code == "BTCUSDT" })
        assertTrue(products.any { it.marketId == marketId && it.type == "SPOT" && it.code == "ETHUSDT" })
        assertFalse(products.any { it.marketId == marketId && it.type == "SPOT" && it.code == "XXXUSDT" })
    }

    @Test
    fun `저장된 상품코드 조회하기`() = runBlocking {
        assertEquals(
            listOf("BTCUSDT", "ETHUSDT"),
            adapter.findProducts(
                marketIds = listOf(marketId),
                productType = ProductType.SPOT,
                productCodes = listOf("BTCUSDT", "ETHUSDT")
            ).map { it.code }
        )
    }

    @Test
    fun `저장되지 않은 상품코드 조회하기`() = runBlocking {
        assertEquals(
            emptyList<String>(),
            adapter.findProducts(
                marketIds = listOf(marketId),
                productType = ProductType.SPOT,
                productCodes = listOf("XXXUSDT")
            ).map { it.code }
        )
    }

    @Test
    fun `저장된 상품코드와 저장되지 않은 상품코드 함께 조회하기`() = runBlocking {
        assertEquals(
            listOf("BTCUSDT"),
            adapter.findProducts(
                marketIds = listOf(marketId),
                productType = ProductType.SPOT,
                productCodes = listOf("BTCUSDT", "XXXUSDT")
            ).map { it.code }
        )
    }
}
