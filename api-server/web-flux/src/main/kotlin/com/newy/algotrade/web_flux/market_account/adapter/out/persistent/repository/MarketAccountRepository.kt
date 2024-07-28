package com.newy.algotrade.web_flux.market_account.adapter.out.persistent.repository

import com.newy.algotrade.domain.market_account.MarketAccount
import com.newy.algotrade.domain.market_account.MarketServer
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MarketAccountRepository : CoroutineCrudRepository<MarketAccountR2dbcEntity, Long> {
    suspend fun existsByMarketServerIdAndAppKeyAndAppSecret(
        marketServerId: Long,
        appKey: String,
        appSecret: String
    ): Boolean
}

@Table("market_server_account")
data class MarketAccountR2dbcEntity(
    @Id val id: Long = 0,
    @Column("users_id") val userId: Long,
    val marketServerId: Long,
    val displayName: String,
    val appKey: String,
    val appSecret: String,
) {
    constructor(domainEntity: MarketAccount) : this(
        userId = domainEntity.userId,
        marketServerId = domainEntity.marketServer.id,
        displayName = domainEntity.displayName,
        appKey = domainEntity.appKey,
        appSecret = domainEntity.appSecret,
    )

    fun toDomainEntity(marketServer: MarketServer) = MarketAccount(
        id = id,
        userId = userId,
        marketServer = marketServer,
        displayName = displayName,
        appKey = appKey,
        appSecret = appSecret,
    )
}