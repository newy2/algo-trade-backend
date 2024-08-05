package com.newy.algotrade.integration.user_strategy.adapter.out.persistence

import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistence.HasStrategyPersistenceAdapter
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistence.repository.StrategyR2dbcEntity
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistence.repository.StrategyRepository
import helpers.BaseDbTest
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class HasStrategyPersistenceAdapterTest(
    @Autowired val strategyRepository: StrategyRepository,
    @Autowired val adapter: HasStrategyPersistenceAdapter,
) : BaseDbTest() {
    @Test
    fun `DB 에 입력된 데이터 확인`() = runBlocking {
        val strategies = strategyRepository.findAll().toList()

        assertEquals(1, strategies.size)
        assertEquals("BuyTripleRSIStrategy", strategies[0].className)
    }

    @Test
    fun `등록된 strategy class name 으로 조회하는 경우`() = runTransactional {
        val savedStrategy = strategyRepository.save(
            StrategyR2dbcEntity(
                className = "SomethingStrategyClass",
                nameKo = "테스트",
                nameEn = "test",
            )
        )
        val savedStrategyClassName = savedStrategy.className

        assertTrue(adapter.hasStrategyByClassName(savedStrategyClassName))
    }

    @Test
    fun `등록하지 않은 strategy class name 으로 조회하는 경우`() = runBlocking {
        val notSavedStrategyClassName = "NotRegisteredStrategy"
        assertFalse(adapter.hasStrategyByClassName(notSavedStrategyClassName))
    }
}

