package com.newy.algotrade.web_flux.config

import com.newy.algotrade.domain.common.consts.GlobalEnv
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitSingle
import org.springframework.r2dbc.core.flow

@Profile("!test")
@Configuration
open class GlobalEnvConfig {
    @Bean
    open fun globalEnv(
        databaseClient: DatabaseClient,
    ): GlobalEnv = runBlocking {
        val (marketServers, adminUser) =
            arrayListOf(
                async {
                    databaseClient
                        .sql(
                            """
                            SELECT m.code             as market_code
                                 , ms.web_server_host as web_url
                                 , ms.web_socket_host as web_socket_url
                                 , msa.app_key        as api_key
                                 , msa.app_secret     as api_secret
                            FROM   market_server ms
                            INNER JOIN market m on m.id = ms.market_id
                            LEFT OUTER JOIN market_server_account msa on ms.id = msa.market_server_id
                            WHERE  ms.prod_server_yn = 'Y';
                        """.trimIndent()
                        )
                        .fetch()
                        .flow()
                        .toList()
                },
                async {
                    databaseClient
                        .sql(
                            """
                            SELECT id
                            FROM   users
                            WHERE  email = 'admin'
                        """.trimIndent()
                        )
                        .fetch()
                        .awaitSingle()
                }
            ).awaitAll().let {
                Pair(it[0] as List<Map<String, String>>, it[1] as Map<String, Long>)
            }

        val byBitInfo = marketServers.find { it["market_code"] == "BY_BIT" }!!
        val lsSecInfo = marketServers.find { it["market_code"] == "LS_SEC" }!!

        GlobalEnv.initialize(
            byBitWebUrl = byBitInfo["web_url"] as String,
            byBitWebSocketUrl = byBitInfo["web_socket_url"] as String,

            lsSecWebUrl = lsSecInfo["web_url"] as String,
            lsSecWebSocketUrl = lsSecInfo["web_socket_url"] as String,
            lsSecApiKey = lsSecInfo["api_key"] as String,
            lsSecApiSecret = lsSecInfo["api_secret"] as String,

            adminUserId = adminUser["id"] as Long,
        )
        return@runBlocking GlobalEnv
    }
}

@Profile("test")
@Configuration
open class GlobalEnvConfigForTesting {
    @Bean
    open fun globalEnv(
        databaseClient: DatabaseClient,
    ): GlobalEnv = GlobalEnv.initialize(
        byBitWebUrl = "",
        byBitWebSocketUrl = "",
        lsSecWebUrl = "",
        lsSecWebSocketUrl = "",
        lsSecApiKey = "",
        lsSecApiSecret = "",
        adminUserId = 0,
    ).let { GlobalEnv }
}