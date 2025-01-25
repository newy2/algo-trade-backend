package com.newy.algotrade.integration.notification.adapter.out.persistence

import com.newy.algotrade.notification.adapter.out.persistence.NotificationSendMessageAdapter
import com.newy.algotrade.notification.domain.NotificationApp
import com.newy.algotrade.notification.domain.NotificationSendMessage
import com.newy.algotrade.notification.domain.Webhook
import helpers.spring.BaseDataR2dbcTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.r2dbc.core.awaitSingle
import kotlin.test.assertEquals

class NotificationSendMessageAdapterTest(
    @Autowired private val adapter: NotificationSendMessageAdapter,
    @Autowired private val databaseClient: DatabaseClient,
) : BaseDataR2dbcTest() {
    @Test
    fun `NotificationSendMessage 저장하기`() = runTransactional {
        val userId = insertUserByEmail("user1@test.com")
        val notificationAppId = insertNotificationApp(userId = userId, useYn = "Y")

        val sendMessageId = adapter.saveNotificationSendMessage(
            NotificationSendMessage(
                notificationApp = newNotificationApp(notificationAppId),
                requestMessage = "request",
                responseMessage = "response",
                status = NotificationSendMessage.Status.SUCCEED
            )
        )

        val results = selectNotificationSendMessageById(sendMessageId)

        assertEquals(sendMessageId, results["id"])
        assertEquals(notificationAppId, results["user_notification_app_id"])
        assertEquals("request", results["request_message"])
        assertEquals("response", results["response_message"])
        assertEquals("SUCCEED", results["status"])
    }

    private fun newNotificationApp(notificationAppId: Long) =
        NotificationApp(
            id = notificationAppId,
            webhook = Webhook.from(
                type = "SLACK",
                url = "url",
            )
        )

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

    private suspend fun selectNotificationSendMessageById(id: Long): Map<String, Any> {
        return databaseClient
            .sql(
                """
                    SELECT id
                         , user_notification_app_id
                         , request_message
                         , response_message
                         , status
                    FROM   user_notification_send_message
                    WHERE  id = :id
                """.trimIndent()
            )
            .bind("id", id)
            .fetch()
            .awaitSingle()
    }
}