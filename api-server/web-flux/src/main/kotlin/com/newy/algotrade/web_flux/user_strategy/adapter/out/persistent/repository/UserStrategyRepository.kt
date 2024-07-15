package com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.repository

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserStrategyRepository : CoroutineCrudRepository<UserStrategyEntity, Long> {
    suspend fun existsByMarketAccountIdAndStrategyIdAndProductType(
        marketAccountId: Long,
        strategyId: Long,
        productType: String
    ): Boolean
}

@Table("user_trade_strategy")
data class UserStrategyEntity(
    @Id val id: Long = 0,
    @Column("market_server_account_id") val marketAccountId: Long,
    @Column("trade_strategy_id") val strategyId: Long,
    val productType: String,
    val productCategory: String,
)