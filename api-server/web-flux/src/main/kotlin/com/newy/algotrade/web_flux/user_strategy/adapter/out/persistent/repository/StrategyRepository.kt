package com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.repository

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface StrategyRepository : CoroutineCrudRepository<StrategyR2dbcEntity, Long> {
    suspend fun existsByClassName(className: String): Boolean
    suspend fun findByClassName(className: String): Long?
}

@Table("trade_strategy")
data class StrategyR2dbcEntity(
    @Id val id: Long = 0,
    val className: String = "",
    val nameKo: String = "",
    val nameEn: String = "",
)