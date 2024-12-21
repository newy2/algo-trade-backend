package com.newy.algotrade.web_flux.market_account.adapter.out.persistence.repository

import com.newy.algotrade.market_account.domain.MarketServer
import org.springframework.data.annotation.Id
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MarketServerRepository : CoroutineCrudRepository<MarketServerR2dbcEntity, Long> {
    @Query(
        """
        SELECT ms.id
             , ms.market_id
        FROM   market_server ms
        INNER JOIN market m ON ms.market_id = m.id
        WHERE  ms.prod_server_yn = CASE WHEN :isProductionServer THEN 'Y' ELSE 'N' END
        AND    m.code = :marketCode
        ;
    """
    )
    suspend fun findByMarketCodeAndIsProductionServer(
        marketCode: String,
        isProductionServer: Boolean,
    ): MarketServerR2dbcEntity?
}

@Table("market_server")
data class MarketServerR2dbcEntity(
    @Id val id: Long = 0,
    val marketId: Long,
) {
    fun toDomainEntity() =
        MarketServer(
            id = id,
            marketId = marketId,
        )
}