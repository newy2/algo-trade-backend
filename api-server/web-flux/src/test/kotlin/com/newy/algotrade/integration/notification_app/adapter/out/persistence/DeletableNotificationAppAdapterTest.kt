package com.newy.algotrade.integration.notification_app.adapter.out.persistence

import com.newy.algotrade.notification_app.adapter.out.persistence.DeletableNotificationAppAdapter
import com.newy.algotrade.notification_app.domain.DeletableNotificationApp
import helpers.spring.BaseDataR2dbcTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.r2dbc.core.awaitSingle
import org.springframework.r2dbc.core.awaitSingleOrNull
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DeletableNotificationAppAdapterTest(
    @Autowired private val adapter: DeletableNotificationAppAdapter,
    @Autowired private val databaseClient: DatabaseClient,
) : BaseDataR2dbcTest() {
    @Test
    fun `user_notification_app 테이블이 use_yn 이 'N' 인 경우 null 을 리턴한다`() = runTransactional {
        val userId = insertUserByEmail("user@test.com")
        val notificationAppId = insertNotificationApp(userId = userId, useYn = "N")

        val notificationApp = adapter.findById(notificationAppId)

        assertNull(notificationApp)
    }

    @Test
    fun `user_notification_app 테이블이 use_yn 이 'Y' 인 경우 도메인 모델을 리턴한다`() = runTransactional {
        val userId = insertUserByEmail("user@test.com")
        val notificationAppId = insertNotificationApp(userId = userId, useYn = "Y")

        val notificationApp = adapter.findById(notificationAppId)

        assertEquals(
            DeletableNotificationApp(
                id = notificationAppId,
                userId = userId
            ),
            notificationApp
        )
    }

    @Test
    fun `NotificationApp 을 삭제하면 user_notification_app 테이블은 소프트 삭제하고, user_notification_app_verify_code 테이블은 하드 삭제한다`() =
        runTransactional {
            val userId = insertUserByEmail("user@test.com")
            val notificationAppId = insertNotificationApp(userId = userId, useYn = "Y")
            insertNotificationAppVerifyCode(notificationAppId = notificationAppId, verifyYn = "Y")

            assertEquals("Y", selectNotificationApp(userId)["use_yn"])
            assertNotNull(selectNotificationAppVerifyCode(notificationAppId))

            adapter.deleteById(notificationAppId)

            assertEquals("N", selectNotificationApp(userId)["use_yn"])
            assertNull(selectNotificationAppVerifyCode(notificationAppId))
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

    private suspend fun insertNotificationApp(
        userId: Long,
        useYn: String,
        type: String = "SLACK",
        url: String = "https://hooks.slack.com/services/1111",
    ): Long {
        databaseClient
            .sql("INSERT INTO user_notification_app (user_id, type, url, use_yn) VALUES (:userId, :type, :url, :useYn)")
            .bind("userId", userId)
            .bind("type", type)
            .bind("url", url)
            .bind("useYn", useYn)
            .fetch()
            .awaitRowsUpdated()

        return selectNotificationApp(userId)["id"] as Long
    }

    private suspend fun selectNotificationApp(userId: Long): Map<String, Any> {
        return databaseClient
            .sql("SELECT * FROM user_notification_app WHERE user_id = :userId")
            .bind("userId", userId)
            .fetch()
            .awaitSingle()
    }

    private suspend fun insertNotificationAppVerifyCode(
        notificationAppId: Long,
        verifyYn: String,
    ): Long {
        databaseClient
            .sql("INSERT INTO user_notification_app_verify_code (user_notification_app_id, verify_yn, verify_code, expired_at) VALUES (:notificationAppId, :verifyYn, 'ABCDE', now())")
            .bind("notificationAppId", notificationAppId)
            .bind("verifyYn", verifyYn)
            .fetch()
            .awaitRowsUpdated()

        return selectNotificationAppVerifyCode(notificationAppId)!!["id"] as Long
    }

    private suspend fun selectNotificationAppVerifyCode(notificationAppId: Long): Map<String, Any>? {
        return databaseClient
            .sql("SELECT * FROM user_notification_app_verify_code WHERE user_notification_app_id = :notificationAppId")
            .bind("notificationAppId", notificationAppId)
            .fetch()
            .awaitSingleOrNull()
    }
}