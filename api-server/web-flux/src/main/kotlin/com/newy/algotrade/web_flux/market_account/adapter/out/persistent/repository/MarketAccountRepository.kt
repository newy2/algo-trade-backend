package com.newy.algotrade.web_flux.market_account.adapter.out.persistent.repository

import com.newy.algotrade.domain.market_account.SetMarketAccount
import org.springframework.data.annotation.Id
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MarketAccountRepository : CoroutineCrudRepository<MarketAccountR2dbcEntity, Long> {
    @Query(
        """
        SELECT msa.id
        FROM   market_server_account msa
        INNER JOIN market_server ms ON msa.market_server_id = ms.id
        INNER JOIN market m ON ms.market_id = m.id
        WHERE  ms.prod_server_yn = CASE WHEN :isProductionServer THEN 'Y' ELSE 'N' END
        AND    m.code = :code
        AND    msa.app_key = :appKey
        AND    msa.app_secret = :appSecret
        ;
    """
    )
    suspend fun getMarketAccountId(
        isProductionServer: Boolean,
        code: String,
        appKey: String,
        appSecret: String,
    ): Long?

    @Modifying
    @Query(
        """
        INSERT INTO market_server_account (
                                            users_id
                                          , market_server_id
                                          , display_name
                                          , app_key
                                          , app_secret
                                          )
        VALUES                            (
                                            (SELECT id FROM users WHERE email = 'admin') -- TODO GlobalEnv.ADMIN_USER_ID
                                          , (
                                             SELECT ms.id
                                             FROM   market_server ms
                                             INNER JOIN market m ON m.id = ms.market_id
                                             WHERE  ms.prod_server_yn = CASE WHEN :isProductionServer THEN 'Y' ELSE 'N' END
                                             AND    m.code = :code
                                            )
                                          , :displayName
                                          , :appKey
                                          , :appSecret
                                          );
    """
    )
    suspend fun setMarketAccount(
        isProductionServer: Boolean,
        code: String,
        appKey: String,
        appSecret: String,
        displayName: String,
    ): Boolean

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
    constructor(domainEntity: SetMarketAccount) : this(
        userId = domainEntity.userId,
        marketServerId = domainEntity.marketServer.id,
        displayName = domainEntity.displayName,
        appKey = domainEntity.appKey,
        appSecret = domainEntity.appSecret,
    )
}