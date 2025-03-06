package com.newy.algotrade.product.adapter.out.persistence.repository

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository("MarketR2dbcRepositoryForProductPackage")
interface MarketR2dbcRepository : CoroutineCrudRepository<MarketR2dbcEntity, Long> {
    suspend fun findByIdIn(ids: Collection<Long>): List<MarketR2dbcEntity>
    suspend fun findByCodeIsNotNull(): List<MarketR2dbcEntity>
}

@Table("market")
data class MarketR2dbcEntity(
    @Id val id: Long,
    val code: String,
    val nameKo: String,
)