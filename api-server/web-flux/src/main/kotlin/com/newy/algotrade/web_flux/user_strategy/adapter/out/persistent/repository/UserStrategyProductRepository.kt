package com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.repository

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserStrategyProductRepository : CoroutineCrudRepository<UserStrategyProductR2dbcEntity, Long>

@Table("user_trade_strategy_product")
data class UserStrategyProductR2dbcEntity(
    @Id val id: Long = 0,
    @Column("user_trade_strategy_id") val userStrategyId: Long,
    val productId: Long,
    val sort: Int,
)