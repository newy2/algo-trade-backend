package com.newy.algotrade.run_strategy.adapter.out.persistence.repository

import com.newy.algotrade.common.consts.Market
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface MarketRepositoryForRunStrategy : CoroutineCrudRepository<MarketR2dbcEntity, Long> {
    suspend fun findByCode(code: Market): MarketR2dbcEntity?
}

@Table("market")
data class MarketR2dbcEntity(
    @Id val id: Long = 0,
    val code: String
)