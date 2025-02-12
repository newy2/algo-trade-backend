package com.newy.algotrade.integration.setting.adapter.out.persistence

import com.newy.algotrade.setting.adapter.out.persistence.NotificationAppAdapter
import com.newy.algotrade.setting.domain.NotificationApp
import helpers.spring.BaseDataR2dbcTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.r2dbc.core.awaitSingle
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NotificationAppAdapterTest(
    @Autowired private val adapter: NotificationAppAdapter,
    @Autowired private val databaseClient: DatabaseClient,
) : BaseDataR2dbcTest() {
    @Test
    fun `저장하지 않은 NotificationApp 을 조회하는 경우`() = runTransactional {
        val userId = insertUserByEmail("user1@test.com")

        assertNull(adapter.getNotificationApp(userId))
    }

    @Test
    fun `소프트 삭제된 NotificationApp 을 조회하는 경우`() = runTransactional {
        val userId = insertUserByEmail("user1@test.com")
        insertNotificationApp(userId = userId, useYn = "N")

        assertNull(adapter.getNotificationApp(userId))
    }

    @Test
    fun `verify 가 완료되지 않은 NotificationApp 을 조회하는 경우`() = runTransactional {
        val userId = insertUserByEmail("user1@test.com")
        val notificationAppId = insertNotificationApp(
            userId = userId,
            useYn = "Y",
            type = "TYPE",
            url = "URL",
        )
        val verifyCodeExpiredAt = LocalDateTime.now().plusMinutes(3)
        insertNotificationAppVerifyCode(
            notificationAppId = notificationAppId,
            verifyYn = "N",
            expiredAt = verifyCodeExpiredAt,
        )

        assertEquals(
            NotificationApp(
                id = notificationAppId,
                webhookType = "TYPE",
                webhookUrl = "URL",
                isVerified = false,
                verifyCodeExpiredAt = verifyCodeExpiredAt,
            ),
            adapter.getNotificationApp(userId)
        )
    }

    @Test
    fun `verify 가 완료된 NotificationApp 을 조회하는 경우`() = runTransactional {
        val userId = insertUserByEmail("user1@test.com")
        val notificationAppId = insertNotificationApp(
            userId = userId,
            useYn = "Y",
            type = "TYPE",
            url = "URL",
        )
        val verifyCodeExpiredAt = LocalDateTime.now()
        insertNotificationAppVerifyCode(
            notificationAppId = notificationAppId,
            verifyYn = "Y",
            expiredAt = verifyCodeExpiredAt,
        )

        assertEquals(
            NotificationApp(
                id = notificationAppId,
                webhookType = "TYPE",
                webhookUrl = "URL",
                isVerified = true,
                verifyCodeExpiredAt = verifyCodeExpiredAt,
            ),
            adapter.getNotificationApp(userId)
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

        return selectNotificationAppIdByUserId(userId)
    }

    private suspend fun selectNotificationAppIdByUserId(userId: Long): Long {
        val user = databaseClient
            .sql("SELECT id FROM user_notification_app WHERE user_id = :userId")
            .bind("userId", userId)
            .fetch()
            .awaitSingle()

        return user["id"] as Long
    }

    private suspend fun insertNotificationAppVerifyCode(
        notificationAppId: Long,
        verifyYn: String,
        expiredAt: LocalDateTime,
    ): Long {
        databaseClient
            .sql("INSERT INTO user_notification_app_verify_code (user_notification_app_id, verify_yn, expired_at, verify_code) VALUES (:notificationAppId, :verifyYn, :expiredAt, 'ABCDE')")
            .bind("notificationAppId", notificationAppId)
            .bind("verifyYn", verifyYn)
            .bind("expiredAt", expiredAt)
            .fetch()
            .awaitRowsUpdated()

        return notificationAppId
    }
}
