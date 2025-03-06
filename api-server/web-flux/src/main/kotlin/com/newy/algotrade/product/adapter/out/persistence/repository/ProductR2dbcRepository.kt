package com.newy.algotrade.product.adapter.out.persistence.repository

import com.newy.algotrade.common.consts.ProductType
import com.newy.algotrade.product.adapter.out.persistence.getMarketCode
import com.newy.algotrade.product.adapter.out.persistence.getMarketId
import com.newy.algotrade.product.domain.RegisterProduct
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository("ProductR2dbcRepositoryForProductPackage")
interface ProductR2dbcRepository : CoroutineCrudRepository<ProductR2dbcEntity, Long> {
    suspend fun findByUseYn(useYn: String = "Y"): List<ProductR2dbcEntity>

}

@Table("product")
data class ProductR2dbcEntity(
    @Id val id: Long = 0,
    val marketId: Long,
    val type: String,
    val code: String,
    val name: String,
    val useYn: String,
    @CreatedDate val createdAt: LocalDateTime? = null,
    @LastModifiedDate val updatedAt: LocalDateTime? = null,
) {
    constructor(domainModel: RegisterProduct, markets: List<MarketR2dbcEntity>) : this(
        marketId = markets.getMarketId(domainModel.marketCode),
        type = domainModel.type.name,
        code = domainModel.code,
        name = domainModel.name,
        useYn = "Y",
    )

    fun toDomainModel(markets: List<MarketR2dbcEntity>) = RegisterProduct(
        marketCode = markets.getMarketCode(marketId),
        type = ProductType.valueOf(type),
        code = code,
        name = name,
    )
}