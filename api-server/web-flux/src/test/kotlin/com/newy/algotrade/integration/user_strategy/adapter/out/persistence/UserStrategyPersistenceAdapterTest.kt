package com.newy.algotrade.integration.user_strategy.adapter.out.persistence

import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductCategory
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.web_flux.market_account.adapter.out.persistent.MarketAccountRepository
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.UserStrategyPersistenceAdapter
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.repository.StrategyEntity
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.repository.StrategyRepository
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.repository.UserStrategyEntity
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.repository.UserStrategyRepository
import helpers.BaseDbTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException

class UserStrategyPersistenceAdapterTest(
    @Autowired private val marketAccountRepository: MarketAccountRepository,
    @Autowired private val strategyRepository: StrategyRepository,
    @Autowired private val userStrategyRepository: UserStrategyRepository,
    @Autowired private val adapter: UserStrategyPersistenceAdapter,
) : BaseDbTest() {
    @Test
    fun `userStrategy 등록하기`() = runTransactional {
        val (marketAccountId, strategyClassName, strategyId) = setInitData()

        val userStrategyId = adapter.setUserStrategy(
            marketServerAccountId = marketAccountId,
            strategyClassName = strategyClassName,
            productType = ProductType.SPOT,
            productCategory = ProductCategory.USER_PICK,
        )

        assertEquals(
            UserStrategyEntity(
                id = userStrategyId,
                marketAccountId = marketAccountId,
                strategyId = strategyId,
                productType = ProductType.SPOT.name,
                productCategory = ProductCategory.USER_PICK.name
            ),
            userStrategyRepository.findById(userStrategyId)!!
        )
    }

    @Test
    fun `등록한 userStrategy 확인하기`() = runTransactional {
        val (marketAccountId, strategyClassName) = setInitData()

        val beforeSaved = adapter.hasUserStrategy(
            marketServerAccountId = marketAccountId,
            strategyClassName = strategyClassName,
            productType = ProductType.SPOT,
        )

        adapter.setUserStrategy(
            marketServerAccountId = marketAccountId,
            strategyClassName = strategyClassName,
            productType = ProductType.SPOT,
            productCategory = ProductCategory.USER_PICK,
        )

        val afterSaved = adapter.hasUserStrategy(
            marketServerAccountId = marketAccountId,
            strategyClassName = strategyClassName,
            productType = ProductType.SPOT,
        )

        assertFalse(beforeSaved)
        assertTrue(afterSaved)
    }

    @Test
    fun `중복된 userStrategy 를 등록하는 경우`() = runTransactional {
        // TODO Remove this? ("등록한 userStrategy 확인하기" 테스트 때문에 지워도 될듯)
        val (marketAccountId, strategyClassName) = setInitData()

        adapter.setUserStrategy(
            marketServerAccountId = marketAccountId,
            strategyClassName = strategyClassName,
            productType = ProductType.SPOT,
            productCategory = ProductCategory.USER_PICK,
        )

        assertThrows<DuplicateKeyException> {
            val sameMarketAccountId = marketAccountId
            val sameStrategyClassName = strategyClassName
            val sameProductType = ProductType.SPOT

            adapter.setUserStrategy(
                marketServerAccountId = sameMarketAccountId,
                strategyClassName = sameStrategyClassName,
                productType = sameProductType,
                productCategory = ProductCategory.TOP_TRADING_VALUE,
            )
        }
    }

    private suspend fun setInitData(): Triple<Long, String, Long> {
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

        val savedStrategy = strategyRepository.save(
            StrategyEntity(
                id = 0,
                className = "SomethingStrategyClass",
                nameKo = "테스트",
                nameEn = "test",
            )
        )

        return Triple(marketAccountId, savedStrategy.className, savedStrategy.id)
    }
}