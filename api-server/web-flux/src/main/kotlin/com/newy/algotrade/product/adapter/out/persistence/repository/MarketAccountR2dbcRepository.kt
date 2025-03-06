package com.newy.algotrade.product.adapter.out.persistence.repository

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository("MarketAccountR2dbcRepositoryForProductPackage")
interface MarketAccountR2dbcRepository : CoroutineCrudRepository<MarketAccountR2dbcEntity, Long> {
    suspend fun findByUserIdAndUseYnOrderByIdAsc(userId: Long, useYn: String = "Y"): List<MarketAccountR2dbcEntity>
}

@Table("market_account")
data class MarketAccountR2dbcEntity(
    @Id val id: Long,
    val userId: Long,
    val marketId: Long,
    val appKey: String,
    val appSecret: String,
    val useYn: String
)