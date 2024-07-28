package com.newy.algotrade.integration.user_strategy.adapter.out.persistence

import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductCategory
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.web_flux.market_account.adapter.out.persistent.repository.MarketAccountRepository
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.SetUserStrategyProductPersistenceAdapter
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.repository.*
import helpers.BaseDbTest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class SetUserStrategyProductPersistenceAdapterTest(
    @Autowired private val marketRepository: MarketRepositoryForStrategy,
    @Autowired private val productRepository: ProductRepository,
    @Autowired private val marketAccountRepository: MarketAccountRepository,
    @Autowired private val strategyRepository: StrategyRepository,
    @Autowired private val userStrategyRepository: UserStrategyRepository,
    @Autowired private val userStrategyProductRepository: UserStrategyProductRepository,
    @Autowired private val adapter: SetUserStrategyProductPersistenceAdapter,
) : BaseDbTest() {
    @Test
    fun `DB 에 입력된 데이터 확인`() = runBlocking {
        val marketId = marketRepository.findByCode("BY_BIT")!!.id
        val products = productRepository.findAll().toList()
        assertTrue(products.any { it.marketId == marketId && it.type == "SPOT" && it.code == "BTCUSDT" })
        assertTrue(products.any { it.marketId == marketId && it.type == "SPOT" && it.code == "ETHUSDT" })
        assertFalse(products.any { it.marketId == marketId && it.type == "SPOT" && it.code == "XXXUSDT" })
    }

    @Test
    fun `strategy product 등록하기`() = runTransactional {
        val (userStrategyId, productIds) = setInitData(productCodes = listOf("BTCUSDT", "ETHUSDT"))

        val isSaved = adapter.setUserStrategyProducts(
            userStrategyId = userStrategyId,
            productIds = productIds
        )
        val userStrategyProducts = userStrategyProductRepository.findAll().toList()

        assertTrue(isSaved)
        assertEquals(2, userStrategyProducts.size)
        assertEquals(
            UserStrategyProductEntity(
                id = userStrategyProducts[0].id,
                userStrategyId = userStrategyId,
                productId = productIds[0],
                sort = 1
            ),
            userStrategyProducts[0]
        )
        assertEquals(
            UserStrategyProductEntity(
                id = userStrategyProducts[1].id,
                userStrategyId = userStrategyId,
                productId = productIds[1],
                sort = 2
            ),
            userStrategyProducts[1]
        )
    }

    @Test
    fun `strategy product 가 없는 경우`() = runTransactional {
        val (userStrategyId, emptyProductIds) = setInitData(productCodes = emptyList())

        val isSaved = adapter.setUserStrategyProducts(
            userStrategyId = userStrategyId,
            productIds = emptyProductIds
        )
        val products = userStrategyProductRepository.findAll().toList()

        assertFalse(isSaved)
        assertEquals(0, products.size)
    }

    private suspend fun setInitData(productCodes: List<String>): Pair<Long, List<Long>> {
        val marketAccountId = marketAccountRepository.setMarketAccount(
            isProductionServer = false,
            code = Market.BY_BIT.name,
            appKey = "key",
            appSecret = "secret",
            displayName = "test",
        ).let {
            marketAccountRepository.getMarketAccountId(
                isProductionServer = false,
                code = Market.BY_BIT.name,
                appKey = "key",
                appSecret = "secret",
            )!!
        }

        val strategyId = strategyRepository.save(
            StrategyEntity(
                id = 0,
                className = "SomethingStrategyClass",
                nameKo = "테스트",
                nameEn = "test",
            )
        ).let { it.id }

        val userStrategyId = userStrategyRepository.save(
            UserStrategyEntity(
                marketAccountId = marketAccountId,
                strategyId = strategyId,
                productType = ProductType.SPOT.name,
                productCategory = ProductCategory.USER_PICK.name,
                timeFrame = Candle.TimeFrame.M1.name,
            )
        ).id

        val marketId = marketRepository.findByCode("BY_BIT")!!.id
        val productIds = productRepository.findAll()
            .filter { it.marketId == marketId && it.type == "SPOT" && productCodes.contains(it.code) }
            .map { it.id }
            .toList()

        return Pair(userStrategyId, productIds)
    }
}