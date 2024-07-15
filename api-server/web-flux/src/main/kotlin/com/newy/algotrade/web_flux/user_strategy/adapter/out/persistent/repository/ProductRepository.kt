package com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.repository

import kotlinx.coroutines.flow.Flow
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ProductRepository : CoroutineCrudRepository<ProductEntity, Long> {
    suspend fun findByMarketIdInAndTypeAndCodeIn(
        marketIds: Collection<Long>,
        type: String,
        codes: Collection<String>
    ): Flow<ProductEntity>
}

@Table("product")
data class ProductEntity(
    @Id val id: Long = 0,
    val marketId: Long,
    val type: String,
    val code: String,
    val nameKo: String = "",
    val nameEn: String = "",
)