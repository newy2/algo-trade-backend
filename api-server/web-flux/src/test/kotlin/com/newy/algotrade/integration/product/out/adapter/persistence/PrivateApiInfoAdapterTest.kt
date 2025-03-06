package com.newy.algotrade.integration.product.out.adapter.persistence

import com.newy.algotrade.auth.domain.PrivateApiInfo
import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.product.adapter.out.persistence.PrivateApiInfoAdapter
import helpers.spring.BaseDataR2dbcTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.r2dbc.core.awaitSingle
import kotlin.test.assertEquals

class PrivateApiInfoAdapterTest(
    @Autowired private val adapter: PrivateApiInfoAdapter,
    @Autowired private val databaseClient: DatabaseClient,
) : BaseDataR2dbcTest() {
    @Test
    fun `저장하지 않은 PrivateApiInfo 를 조회하는 경우`() = runTransactional {
        val userId = insertUserByEmail("user1@test.com")

        assertEquals(emptyMap(), adapter.findPrivateApiInfos(userId))
    }


    @Test
    fun `소프트 삭제된 MarketAccount 을 조회하는 경우`() = runTransactional {
        val userId = insertUserByEmail("user1@test.com")
        insertMarketAccount(userId = userId, useYn = "N")

        assertEquals(emptyMap(), adapter.findPrivateApiInfos(userId))
    }

    @Test
    fun `저장된 MarketAccount 을 조회하는 경우`() = runTransactional {
        val userId = insertUserByEmail("user1@test.com")
        insertMarketAccount(
            userId = userId,
            useYn = "Y",
            marketCode = "BY_BIT",
            displayName = "ByBit 테스트 계정",
            appKey = "APP KEY1",
            appSecret = "APP SECRET1",
        )
        insertMarketAccount(
            userId = userId,
            useYn = "Y",
            marketCode = "LS_SEC",
            displayName = "LS증권 테스트 계정",
            appKey = "APP KEY2",
            appSecret = "APP SECRET2",
        )

        assertEquals(
            mapOf(
                MarketCode.BY_BIT to PrivateApiInfo(
                    appKey = "APP KEY1",
                    appSecret = "APP SECRET1",
                ),
                MarketCode.LS_SEC to PrivateApiInfo(
                    appKey = "APP KEY2",
                    appSecret = "APP SECRET2",
                )
            ),
            adapter.findPrivateApiInfos(userId)
        )
    }

    @Test
    fun `중복된 Market 의 MarketAccount 는 가장 마지막에 등록된 MarketAccount 를 반환한다`() = runTransactional {
        val userId = insertUserByEmail("user1@test.com")
        insertMarketAccount(
            userId = userId,
            useYn = "Y",
            marketCode = "BY_BIT",
            displayName = "ByBit 테스트 계정1",
            appKey = "APP KEY1",
            appSecret = "APP SECRET1",
        )
        insertMarketAccount(
            userId = userId,
            useYn = "Y",
            marketCode = "BY_BIT",
            displayName = "ByBit 테스트 계정1",
            appKey = "APP KEY2",
            appSecret = "APP SECRET2",
        )

        assertEquals(
            mapOf(
                MarketCode.BY_BIT to PrivateApiInfo(
                    appKey = "APP KEY2",
                    appSecret = "APP SECRET2",
                )
            ),
            adapter.findPrivateApiInfos(userId)
        )
    }

    private suspend fun insertUserByEmail(email: String): Long {
        databaseClient
            .sql("INSERT INTO users (email) VALUES (:email)")
            .bind("email", email)
            .fetch()
            .awaitRowsUpdated()

        return selectUserIdByEmail(email)
    }

    private suspend fun selectUserIdByEmail(email: String): Long {
        val user = databaseClient
            .sql("SELECT id FROM users WHERE email = :email")
            .bind("email", email)
            .fetch()
            .awaitSingle()

        return user["id"] as Long
    }

    private suspend fun insertMarketAccount(
        userId: Long,
        marketCode: String = "BY_BIT",
        displayName: String = "TEST 계정",
        useYn: String,
        appKey: String = "appKey",
        appSecret: String = "appSecret",
        verifyYn: String = "Y"
    ): Long {
        databaseClient
            .sql(
                """
                INSERT INTO market_account (
                                              user_id
                                            , market_id
                                            , display_name
                                            , use_yn
                                            , app_key
                                            , app_secret
                                            , verify_yn
                                            ) VALUES (
                                              :userId
                                            , :marketId
                                            , :displayName
                                            , :useYn
                                            , :appKey
                                            , :appSecret
                                            , :verifyYn
                                            )
            """.trimIndent()
            )
            .bind("userId", userId)
            .bind("marketId", selectMarketIdByMarketCode(marketCode))
            .bind("displayName", displayName)
            .bind("useYn", useYn)
            .bind("appKey", appKey)
            .bind("appSecret", appSecret)
            .bind("verifyYn", verifyYn)
            .fetch()
            .awaitRowsUpdated()

        return selectMarketAccount(
            userId = userId,
            displayName = displayName,
            useYn = useYn,
        )["id"] as Long
    }

    private suspend fun selectMarketAccount(userId: Long, displayName: String, useYn: String): Map<String, Any> {
        return databaseClient
            .sql("SELECT * FROM market_account WHERE user_id = :userId AND display_name = :displayName and use_yn = :useYn")
            .bind("userId", userId)
            .bind("displayName", displayName)
            .bind("useYn", useYn)
            .fetch()
            .awaitSingle()
    }

    private suspend fun selectMarketIdByMarketCode(marketCode: String): Long {
        val market = databaseClient
            .sql("SELECT id FROM market WHERE code = :code")
            .bind("code", marketCode)
            .fetch()
            .awaitSingle()

        return market["id"] as Long
    }
}