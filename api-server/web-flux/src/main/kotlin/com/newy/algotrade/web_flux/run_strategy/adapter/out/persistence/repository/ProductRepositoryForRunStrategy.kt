package com.newy.algotrade.web_flux.run_strategy.adapter.out.persistence.repository

import com.newy.algotrade.common.domain.consts.Market
import com.newy.algotrade.common.domain.consts.ProductType
import org.springframework.data.annotation.Id
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ProductRepositoryForRunStrategy : CoroutineCrudRepository<ProductR2dbcEntity, Long> {
    @Query(
        """
        SELECT p.id
        FROM   product p
        INNER JOIN market m on m.id = p.market_id
        WHERE  m.code = :market
        AND    p.type = :productType
        AND    p.code = :productCode
        AND    p.use_yn = :useYn
    """
    )
    suspend fun findProductId(
        market: Market,
        productType: ProductType,
        productCode: String,
        useYn: String = "Y"
    ): Long?
}

@Table("product")
data class ProductR2dbcEntity(
    @Id val id: Long,
    val marketId: Long,
    val type: String,
    val code: String,
    val useYn: Char,
)