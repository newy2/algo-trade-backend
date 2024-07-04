package com.newy.algotrade.web_flux.market_account.adapter.out.persistent

import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MarketAccountRepository : CoroutineCrudRepository<MarketAccountEntity, Long> {
    @Query(
        """
        select exists(
            select 'X'
            from   market_server_account msa
            inner join market_server ms on msa.market_server_id = ms.id
            inner join market m on ms.market_id = m.id
            where  ms.prod_server_yn = case when :isProductionServer then 'Y' else 'N' end
            and    m.code = :code
            and    msa.app_key = :appKey
            and    msa.app_secret = :appSecret
        );
    """
    )
    suspend fun existsMarketAccount(
        isProductionServer: Boolean,
        code: String,
        appKey: String,
        appSecret: String,
    ): Boolean

    @Modifying
    @Query(
        """
        insert into market_server_account (
                                            users_id
                                          , market_server_id
                                          , display_name
                                          , app_key
                                          , app_secret
                                          )
        values                            (
                                            (select id from users where email = 'admin')
                                          , (
                                             select ms.id
                                             from   market_server ms
                                             inner join market m on m.id = ms.market_id
                                             where  ms.prod_server_yn = case when :isProductionServer then 'Y' else 'N' end
                                             and    m.code = :code
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
    ): Int
}