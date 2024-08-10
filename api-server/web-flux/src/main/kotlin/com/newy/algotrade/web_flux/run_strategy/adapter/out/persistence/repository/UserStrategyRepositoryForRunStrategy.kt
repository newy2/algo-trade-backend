package com.newy.algotrade.web_flux.run_strategy.adapter.out.persistence.repository

import com.newy.algotrade.domain.chart.order.OrderType
import org.springframework.data.annotation.Id
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface StrategyRepositoryForRunStrategy : CoroutineCrudRepository<StrategyR2dbcEntity, Long> {
    @Query(
        """
        SELECT ts.id as id
             , ts.entry_order_type as entry_order_type
        FROM   trade_strategy ts
        INNER JOIN user_trade_strategy uts on ts.id = uts.trade_strategy_id
        WHERE  uts.id = :userStrategyId
    """
    )
    suspend fun findByUserStrategyId(userStrategyId: Long): StrategyR2dbcEntity?
}

@Table("trade_strategy")
data class StrategyR2dbcEntity(
    @Id val id: Long,
    val entryOrderType: OrderType,
)