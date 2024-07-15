package com.newy.algotrade.web_flux.market_account.adapter.out.persistent

import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MarketAccountRepository : CoroutineCrudRepository<MarketAccountEntity, Long> {
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
                                            (SELECT id FROM users WHERE email = 'admin')
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
}