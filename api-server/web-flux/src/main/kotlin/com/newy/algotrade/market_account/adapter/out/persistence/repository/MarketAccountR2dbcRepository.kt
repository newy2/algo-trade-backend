package com.newy.algotrade.market_account.adapter.out.persistence.repository

import com.newy.algotrade.market_account.domain.MarketAccount
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MarketAccountR2dbcRepository : CoroutineCrudRepository<MarketAccountR2dbcEntity, Long> {
    suspend fun existsByUserIdAndDisplayNameAndUseYn(userId: Long, displayName: String, useYn: String = "Y"): Boolean
    suspend fun existsByUserIdAndAppKeyAndAppSecretAndUseYn(
        userId: Long,
        appKey: String,
        appSecret: String,
        useYn: String = "Y"
    ): Boolean
}

@Table("market_account")
data class MarketAccountR2dbcEntity(
    @Id val id: Long = 0,
    val userId: Long,
    val marketId: Long,
    val displayName: String,
    val appKey: String,
    val appSecret: String,
    val useYn: String,
    val verifyYn: String,
) {
    constructor(domainModel: MarketAccount, marketId: Long) : this(
        userId = domainModel.userId,
        marketId = marketId,
        displayName = domainModel.displayName,
        appKey = domainModel.privateApiInfo.appKey,
        appSecret = domainModel.privateApiInfo.appSecret,
        useYn = "Y",
        verifyYn = "Y",
    )
}