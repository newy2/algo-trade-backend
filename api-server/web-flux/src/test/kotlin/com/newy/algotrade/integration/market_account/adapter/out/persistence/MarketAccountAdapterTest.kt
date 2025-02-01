package com.newy.algotrade.integration.market_account.adapter.out.persistence

import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.market_account.adapter.out.persistence.MarketAccountAdapter
import com.newy.algotrade.market_account.domain.MarketAccount
import helpers.spring.BaseDataR2dbcTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.r2dbc.core.awaitSingle
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MarketAccountAdapterTest(
    @Autowired private val adapter: MarketAccountAdapter,
    @Autowired private val databaseClient: DatabaseClient,
) : BaseDataR2dbcTest() {
    @Test
    fun `MarketAccount 중복 여부 확인하기`() = runTransactional {
        val userId = insertUserByEmail("user@test.com")

        assertFalse(
            adapter.existsMarketAccount(
                MarketAccount(
                    userId = userId,
                    displayName = "test",
                    privateApiInfo = MarketAccount.PrivateApiInfo(
                        marketCode = MarketCode.BY_BIT,
                        appKey = "ABCD",
                        appSecret = "1234",
                    )
                )
            )
        )
    }

    @Test
    fun `displayName 가 동일하면 중복이다`() = runTransactional {
        val duplicateDisplayName = "display name"
        val userId = insertUserByEmail("user@test.com")
        insertMarketAccount(
            userId = userId,
            displayName = duplicateDisplayName,
            marketCode = "BY_BIT",
            useYn = "Y"
        )

        assertTrue(
            adapter.existsMarketAccount(
                MarketAccount(
                    userId = userId,
                    displayName = duplicateDisplayName,
                    privateApiInfo = MarketAccount.PrivateApiInfo(
                        marketCode = MarketCode.BY_BIT,
                        appKey = "ABCD",
                        appSecret = "1234",
                    )
                )
            )
        )
    }

    @Test
    fun `소프트 삭제 처리된 데이터는 중복 검사에서 제외한다(displayName 중복)`() = runTransactional {
        val softDeleted = "N"
        val duplicateDisplayName = "display name"
        val userId = insertUserByEmail("user@test.com")
        insertMarketAccount(
            userId = userId,
            displayName = duplicateDisplayName,
            marketCode = "BY_BIT",
            useYn = softDeleted
        )

        assertFalse(
            adapter.existsMarketAccount(
                MarketAccount(
                    userId = userId,
                    displayName = duplicateDisplayName,
                    privateApiInfo = MarketAccount.PrivateApiInfo(
                        marketCode = MarketCode.BY_BIT,
                        appKey = "ABCD",
                        appSecret = "1234",
                    )
                )
            )
        )
    }

    @Test
    fun `appKey 와 appSecret 이 동일하면 중복이다`() = runTransactional {
        val duplicateAppKey = "APP_KEY"
        val duplicateAppSecret = "APP_SECRET"
        val userId = insertUserByEmail("user@test.com")
        insertMarketAccount(
            userId = userId,
            displayName = "display name1",
            marketCode = "BY_BIT",
            appKey = duplicateAppKey,
            appSecret = duplicateAppSecret,
            useYn = "Y"
        )

        assertTrue(
            adapter.existsMarketAccount(
                MarketAccount(
                    userId = userId,
                    displayName = "display name2",
                    privateApiInfo = MarketAccount.PrivateApiInfo(
                        marketCode = MarketCode.BY_BIT,
                        appKey = duplicateAppKey,
                        appSecret = duplicateAppSecret,
                    )
                )
            )
        )
    }

    @Test
    fun `소프트 삭제 처리된 데이터는 중복 검사에서 제외한다(appKey, appSecret 중복)`() = runTransactional {
        val softDeleted = "N"
        val duplicateAppKey = "APP_KEY"
        val duplicateAppSecret = "APP_SECRET"
        val userId = insertUserByEmail("user@test.com")
        insertMarketAccount(
            userId = userId,
            displayName = "display name1",
            marketCode = "BY_BIT",
            appKey = duplicateAppKey,
            appSecret = duplicateAppSecret,
            useYn = softDeleted
        )

        assertFalse(
            adapter.existsMarketAccount(
                MarketAccount(
                    userId = userId,
                    displayName = "display name2",
                    privateApiInfo = MarketAccount.PrivateApiInfo(
                        marketCode = MarketCode.BY_BIT,
                        appKey = duplicateAppKey,
                        appSecret = duplicateAppSecret,
                    )
                )
            )
        )
    }

    @Test
    fun `saveMarketAccount 로 MarketAccount 를 저장한다`() = runTransactional {
        val userId = insertUserByEmail("user@test.com")
        val marketAccount = MarketAccount(
            userId = userId,
            displayName = "display name",
            privateApiInfo = MarketAccount.PrivateApiInfo(
                marketCode = MarketCode.BY_BIT,
                appKey = "ABCD",
                appSecret = "1234",
            )
        )

        assertFalse(adapter.existsMarketAccount(marketAccount))
        adapter.saveMarketAccount(marketAccount)
        assertTrue(adapter.existsMarketAccount(marketAccount))
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
        marketCode: String,
        displayName: String,
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