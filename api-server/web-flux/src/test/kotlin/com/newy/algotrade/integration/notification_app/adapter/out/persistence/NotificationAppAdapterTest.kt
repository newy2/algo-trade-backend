package com.newy.algotrade.integration.notification_app.adapter.out.persistence

import com.newy.algotrade.notification_app.adapter.out.persistence.NotificationAppPersistenceAdapter
import com.newy.algotrade.notification_app.domain.NotificationApp
import com.newy.algotrade.notification_app.domain.Webhook
import helpers.diffSeconds
import helpers.spring.BaseDataR2dbcTest
import kotlinx.coroutines.delay
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.r2dbc.core.awaitSingle
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NotificationAppAdapterTest(
    @Autowired val adapter: NotificationAppPersistenceAdapter,
    @Autowired private val databaseClient: DatabaseClient,
) : BaseDataR2dbcTest() {
    @Test
    fun `저장되지 않은 알림앱 인증코드 조회하기`() = runTransactional {
        val userId = insertUserByEmail("user1@test.com")
        val notificationApp = adapter.findByUserId(userId)

        assertNull(notificationApp)
    }

    @Test
    fun `알림앱 인증코드 저장하기`() = runTransactional {
        val userId = insertUserByEmail("user1@test.com")
        val expireAt = NotificationApp.getDefaultExpiredAt()
        val notificationApp = NotificationApp(
            userId = userId,
            webhook = Webhook(
                type = "SLACK",
                url = "https://hooks.slack.com/services/1111",
            ),
            isVerified = false,
            verifyCode = "ABCDE"
        )

        assertTrue(adapter.save(notificationApp))
        assertEquals(notificationApp, adapter.findByUserId(userId))
        assertEquals(0, diffSeconds(expireAt, notificationApp.expiredAt))
    }

    @Test
    fun `알림앱 인증코드 재발급 후 저장하기`() = runTransactional {
        val userId = insertUserByEmail("user1@test.com")
        val oldNotificationApp = NotificationApp(
            userId = userId,
            webhook = Webhook(
                type = "SLACK",
                url = "https://hooks.slack.com/services/1111",
            ),
            isVerified = false,
            verifyCode = "ABCDE"
        )
        adapter.save(oldNotificationApp)

        delay(1000)
        val newExpireAt = NotificationApp.getDefaultExpiredAt()
        val newNotificationApp = oldNotificationApp.generateVerifyCode()

        assertTrue(adapter.save(newNotificationApp))
        assertEquals(newNotificationApp, adapter.findByUserId(userId))
        assertTrue(oldNotificationApp.expiredAt < newNotificationApp.expiredAt, "유효기간이 갱신된다")
        assertEquals(0, diffSeconds(newExpireAt, newNotificationApp.expiredAt), "유효기간이 갱신된다")
    }

    @Test
    fun `알림앱 인증코드 검증 완료 후 저장하기`() = runTransactional {
        val userId = insertUserByEmail("user1@test.com")
        val oldNotificationApp = NotificationApp(
            userId = userId,
            webhook = Webhook(
                type = "SLACK",
                url = "https://hooks.slack.com/services/1111",
            ),
            isVerified = false,
            verifyCode = "ABCDE"
        )
        adapter.save(oldNotificationApp)

        val newNotificationApp = oldNotificationApp.verify(verifyCode = "ABCDE")
        val isSaved = adapter.save(newNotificationApp)

        assertTrue(isSaved)
        assertFalse(oldNotificationApp.isVerified)
        assertTrue(newNotificationApp.isVerified)
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
}

