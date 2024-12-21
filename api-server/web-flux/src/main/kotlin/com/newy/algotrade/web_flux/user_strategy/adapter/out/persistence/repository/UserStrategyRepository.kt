package com.newy.algotrade.web_flux.user_strategy.adapter.out.persistence.repository

import com.newy.algotrade.user_strategy.domain.SetUserStrategy
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserStrategyRepository : CoroutineCrudRepository<UserStrategyR2dbcEntity, Long> {
    suspend fun existsByMarketAccountIdAndStrategyIdAndProductType(
        marketAccountId: Long,
        strategyId: Long,
        productType: String
    ): Boolean
}

@Table("user_trade_strategy")
data class UserStrategyR2dbcEntity(
    @Id val id: Long = 0,
    @Column("market_server_account_id") val marketAccountId: Long,
    @Column("trade_strategy_id") val strategyId: Long,
    val productType: String,
    val productCategory: String,
    val timeFrame: String,
) {
    constructor(domainEntity: SetUserStrategy, strategyId: Long) : this(
        marketAccountId = domainEntity.setUserStrategyKey.marketServerAccountId,
        strategyId = strategyId,
        productType = domainEntity.setUserStrategyKey.productType.name,
        productCategory = domainEntity.productCategory.name,
        timeFrame = domainEntity.timeFrame.name,
    )
}