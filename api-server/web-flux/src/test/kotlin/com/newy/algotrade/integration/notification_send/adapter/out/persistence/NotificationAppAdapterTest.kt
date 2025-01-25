package com.newy.algotrade.integration.notification_send.adapter.out.persistence

import com.newy.algotrade.notification_send.adapter.out.persistence.NotificationAppAdapter
import com.newy.algotrade.notification_send.domain.NotificationApp
import com.newy.algotrade.notification_send.domain.Webhook
import helpers.spring.BaseDataR2dbcTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.r2dbc.core.awaitSingle
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NotificationAppAdapterTest(
    @Autowired private val adapter: NotificationAppAdapter,
    @Autowired private val databaseClient: DatabaseClient,
) : BaseDataR2dbcTest() {
    private val webhookType = "SLACK"
    private val webhookUrl = "https://hooks.slack.com/services/1111"
    private val fakeWebhook = Webhook.from(
        type = webhookType,
        url = webhookUrl
    )

    @Test
    fun `user_notification_app_verify_code 테이블의 verify_yn 값과 isVerified 값이 일치하면 NotificationApp 을 리턴한다`() =
        runTransactional {
            listOf(
                Pair("Y", true),
                Pair("N", false),
            ).forEachIndexed { index, (verifyYn, isVerified) ->
                val userId = insertUserByEmail("user${index}@test.com")
                val notificationAppId = insertNotificationApp(userId = userId, useYn = "Y").also {
                    insertNotificationAppVerifyCode(notificationAppId = it, verifyYn = verifyYn)
                }

                val notificationApp = adapter.findNotificationApp(
                    userId = userId,
                    isVerified = isVerified
                )

                assertEquals(
                    NotificationApp(
                        id = notificationAppId,
                        webhook = fakeWebhook,
                    ),
                    notificationApp
                )
            }
        }

    @Test
    fun `user_notification_app_verify_code 테이블의 verify_yn 값과 isVerified 값이 일치하지 않으면 null 을 리턴한다`() =
        runTransactional {
            listOf(
                Pair("Y", false),
                Pair("N", true),
            ).forEachIndexed { index, (verifyYn, isVerified) ->
                val userId = insertUserByEmail("user${index}@test.com")
                insertNotificationApp(userId = userId, useYn = "Y").also {
                    insertNotificationAppVerifyCode(notificationAppId = it, verifyYn = verifyYn)
                }

                val notificationApp = adapter.findNotificationApp(
                    userId = userId,
                    isVerified = isVerified
                )

                assertNull(notificationApp)
            }
        }

    @Test
    fun `user_notification_app 테이블의 useYn 이 'N' 이면 null 을 리턴한다`() = runTransactional {
        val useYn = "N"

        val userId = insertUserByEmail("user1@test.com")
        insertNotificationApp(userId = userId, useYn = useYn).also {
            insertNotificationAppVerifyCode(notificationAppId = it, verifyYn = "Y")
        }

        val notificationApp = adapter.findNotificationApp(
            userId = userId,
            isVerified = true
        )

        assertNull(notificationApp)
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
        type: String = webhookType,
        url: String = webhookUrl,
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
    ): Long {
        databaseClient
            .sql("INSERT INTO user_notification_app_verify_code (user_notification_app_id, verify_yn, verify_code, expired_at) VALUES (:notificationAppId, :verifyYn, 'ABCDE', now())")
            .bind("notificationAppId", notificationAppId)
            .bind("verifyYn", verifyYn)
            .fetch()
            .awaitRowsUpdated()

        return notificationAppId
    }
}

