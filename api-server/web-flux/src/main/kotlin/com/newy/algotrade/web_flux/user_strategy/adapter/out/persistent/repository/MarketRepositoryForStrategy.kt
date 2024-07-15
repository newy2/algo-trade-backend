package com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.repository

import org.springframework.data.annotation.Id
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface MarketRepositoryForStrategy : CoroutineCrudRepository<MarketEntity, Long> {
    @Query(
        """
        SELECT m.id AS id
             , m.parent_id AS parent_id
        FROM   market m
        INNER JOIN market_server ms ON m.id = ms.market_id
        INNER JOIN market_server_account msa ON ms.id = msa.market_server_id
        WHERE  msa.id = :marketServerAccountId
    """
    )
    suspend fun findByMarketServerAccountId(marketServerAccountId: Long): MarketEntity?

    suspend fun findByCode(code: String): MarketEntity?
}

@Table("market")
data class MarketEntity(
    @Id val id: Long,
    @Column("parent_id") val parentMarketId: Long,
    val code: String = "",
)