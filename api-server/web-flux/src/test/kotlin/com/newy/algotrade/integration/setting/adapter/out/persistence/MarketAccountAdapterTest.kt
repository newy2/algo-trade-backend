package com.newy.algotrade.integration.setting.adapter.out.persistence

import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.setting.adapter.out.persistence.MarketAccountAdapter
import com.newy.algotrade.setting.domain.MarketAccount
import helpers.spring.BaseDataR2dbcTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.r2dbc.core.awaitSingle
import kotlin.test.assertEquals

class MarketAccountAdapterTest(
    @Autowired private val adapter: MarketAccountAdapter,
    @Autowired private val databaseClient: DatabaseClient,
) : BaseDataR2dbcTest() {
    @Test
    fun `저장하지 않은 MarketAccount 을 조회하는 경우`() = runTransactional {
        val userId = insertUserByEmail("user1@test.com")

        assertEquals(emptyList(), adapter.getMarketAccounts(userId))
    }

    @Test
    fun `소프트 삭제된 MarketAccount 을 조회하는 경우`() = runTransactional {
        val userId = insertUserByEmail("user1@test.com")
        insertMarketAccount(userId = userId, useYn = "N")

        assertEquals(emptyList(), adapter.getMarketAccounts(userId))
    }

    @Test
    fun `저장된 MarketAccount 을 조회하는 경우`() = runTransactional {
        val userId = insertUserByEmail("user1@test.com")
        val marketAccountId = insertMarketAccount(
            userId = userId,
            useYn = "Y",
            marketCode = "BY_BIT",
            displayName = "ByBit 테스트 계정",
            appKey = "APP KEY",
            appSecret = "APP SECRET",
        )

        assertEquals(
            listOf(
                MarketAccount(
                    id = marketAccountId,
                    marketCode = MarketCode.BY_BIT,
                    marketName = "바이빗",
                    displayName = "ByBit 테스트 계정",
                    appKey = "APP KEY",
                    appSecret = "APP SECRET",
                )
            ),
            adapter.getMarketAccounts(userId)
        )
    }

    @Test
    fun `2개 이상의 MarketAccount 를 조회하는 경우, MarketAccount#id 기준으로 오름차순으로 정렬한다`() = runTransactional {
        val userId = insertUserByEmail("user1@test.com")
        val marketAccountId1 = insertMarketAccount(
            userId = userId,
            useYn = "Y",
            marketCode = "BY_BIT",
            displayName = "ByBit 테스트 계정",
            appKey = "APP KEY1",
            appSecret = "APP SECRET1",
        )
        val marketAccountId2 = insertMarketAccount(
            userId = userId,
            useYn = "Y",
            marketCode = "LS_SEC",
            displayName = "LS증권 테스트 계정",
            appKey = "APP KEY2",
            appSecret = "APP SECRET2",
        )

        assertEquals(
            listOf(
                MarketAccount(
                    id = marketAccountId1,
                    marketCode = MarketCode.BY_BIT,
                    marketName = "바이빗",
                    displayName = "ByBit 테스트 계정",
                    appKey = "APP KEY1",
                    appSecret = "APP SECRET1",
                ),
                MarketAccount(
                    id = marketAccountId2,
                    marketCode = MarketCode.LS_SEC,
                    marketName = "LS증권",
                    displayName = "LS증권 테스트 계정",
                    appKey = "APP KEY2",
                    appSecret = "APP SECRET2",
                )
            ),
            adapter.getMarketAccounts(userId)
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