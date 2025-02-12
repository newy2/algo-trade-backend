package com.newy.algotrade.market_account.adapter.out.persistence.repository

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MarketR2dbcRepository : CoroutineCrudRepository<MarketR2dbcEntity, Long> {
    suspend fun findByCode(code: String): MarketR2dbcEntity?
}

@Table("market")
data class MarketR2dbcEntity(
    @Id val id: Long,
    val code: String,
)