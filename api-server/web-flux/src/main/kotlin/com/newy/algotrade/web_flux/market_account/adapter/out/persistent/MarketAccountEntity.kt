package com.newy.algotrade.web_flux.market_account.adapter.out.persistent

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("market_server_account")
data class MarketAccountEntity(
    @Id val id: Long = 0,
    @Column("users_id") val userId: Long,
    val marketServerId: Long,
    val displayName: String,
    val appKey: String,
    val appSecret: String,
)