package com.newy.algotrade.setting.adapter.out.persistence.repository

import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.setting.domain.MarketAccount
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository("MarketAccountR2dbcRepositoryForSettingPackage")
interface MarketAccountR2dbcRepository : CoroutineCrudRepository<MarketAccountR2dbcEntity, Long> {
    suspend fun findByUserIdAndUseYnOrderByIdAsc(userId: Long, useYn: String = "Y"): List<MarketAccountR2dbcEntity>
}

@Table("market_account")
data class MarketAccountR2dbcEntity(
    @Id val id: Long,
    val userId: Long,
    val marketId: Long,
    val displayName: String,
    val appKey: String,
    val appSecret: String,
    val useYn: String
) {
    fun toDomainModel(markets: List<MarketR2dbcEntity>): MarketAccount =
        markets.find { it.id == marketId }!!.let { market ->
            MarketAccount(
                id = id,
                marketCode = MarketCode.valueOf(market.code),
                marketName = market.nameKo,
                displayName = displayName,
                appKey = appKey,
                appSecret = appSecret,
            )
        }
}