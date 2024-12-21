package com.newy.algotrade.unit.strategy.out.persistence.repository

import com.newy.algotrade.chart.domain.order.OrderType
import com.newy.algotrade.strategy.adapter.out.persistence.repository.StrategyR2dbcEntity
import com.newy.algotrade.strategy.domain.Strategy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StrategyR2dbcEntityTest {
    private val persistenceEntity = StrategyR2dbcEntity(
        id = 0,
        className = "SomethingClassName",
        entryType = OrderType.BUY,
        nameKo = "",
        nameEn = "",
    )

    @Test
    fun toDomainEntity() {
        assertEquals(
            Strategy(
                id = 0,
                className = "SomethingClassName",
                entryType = OrderType.BUY
            ),
            persistenceEntity.toDomainEntity()
        )
    }
}