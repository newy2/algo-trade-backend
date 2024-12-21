package com.newy.algotrade.strategy.adapter.out.persistence.repository

import com.newy.algotrade.chart.domain.order.OrderType
import com.newy.algotrade.strategy.domain.Strategy
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface StrategyRepository : CoroutineCrudRepository<StrategyR2dbcEntity, Long> {
    suspend fun existsByClassName(className: String): Boolean
    suspend fun findByClassName(className: String): StrategyR2dbcEntity?
}

@Table("trade_strategy")
data class StrategyR2dbcEntity(
    @Id val id: Long = 0,
    val className: String = "",
    @Column("entry_order_type") val entryType: OrderType,
    val nameKo: String = "",
    val nameEn: String = "",
) {
    fun toDomainEntity() = Strategy(
        id = id,
        className = className,
        entryType = entryType
    )
}