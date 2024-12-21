package com.newy.algotrade.integration.strategy.adapter.out.persistence

import com.newy.algotrade.chart.domain.order.OrderType
import com.newy.algotrade.strategy.adapter.out.persistence.StrategyPersistenceAdapter
import com.newy.algotrade.strategy.adapter.out.persistence.repository.StrategyR2dbcEntity
import com.newy.algotrade.strategy.adapter.out.persistence.repository.StrategyRepository
import helpers.spring.BaseDbTest
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class StrategyPersistenceAdapterTest(
    @Autowired val strategyRepository: StrategyRepository,
    @Autowired val adapter: StrategyPersistenceAdapter,
) : BaseDbTest() {
    @Test
    fun `DB 에 입력된 데이터 확인`() = runBlocking {
        val strategies = strategyRepository.findAll().toList()

        assertEquals(1, strategies.size)
        strategies[0].let {
            assertEquals("BuyTripleRSIStrategy", it.className)
            assertEquals(OrderType.BUY, it.entryType)
        }
    }

    @Test
    fun `등록된 strategy class name 으로 조회하는 경우`() = runTransactional {
        val savedStrategy = strategyRepository.save(
            StrategyR2dbcEntity(
                className = "SomethingStrategyClass",
                entryType = OrderType.BUY,
                nameKo = "테스트",
                nameEn = "test",
            )
        )
        val savedStrategyClassName = savedStrategy.className

        assertTrue(adapter.existsStrategy(savedStrategyClassName))
    }

    @Test
    fun `등록하지 않은 strategy class name 으로 조회하는 경우`() = runBlocking {
        val notSavedStrategyClassName = "NotRegisteredStrategy"
        assertFalse(adapter.existsStrategy(notSavedStrategyClassName))
    }
}

