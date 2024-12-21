package com.newy.algotrade.integration.notification.adapter.out.persistence

import com.newy.algotrade.common.domain.consts.NotificationAppType
import com.newy.algotrade.notification.adapter.out.persistence.NotificationAppAdapter
import com.newy.algotrade.notification.domain.NotificationApp
import helpers.BaseDbTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitSingle

class NotificationAppAdapterTest(
    @Autowired private val databaseClient: DatabaseClient,
    @Autowired private val adapter: NotificationAppAdapter,
) : BaseDbTest() {
    @Test
    fun `사전 데이터 확인`() = runTransactional {
        assertTrue(getAdminUserId() > 0)
    }

    @Test
    fun `알림 앱 등록 여부 확인하기`() = runTransactional {
        val userId = getAdminUserId()

        assertFalse(adapter.existsNotificationApp(userId))
    }

    @Test
    fun `알림 앱 등록하기`() = runTransactional {
        val userId = getAdminUserId()

        val isSaved = adapter.saveNotificationApp(
            NotificationApp(
                userId = userId,
                type = NotificationAppType.SLACK,
                url = "${NotificationAppType.SLACK.host}/XXXX/YYY"
            )
        )

        assertTrue(isSaved)
        assertTrue(adapter.existsNotificationApp(userId))
    }

    private suspend fun getAdminUserId(): Long {
        // TODO UserRepository 구현 시, 리팩토링 하기
        val adminUser = databaseClient
            .sql(
                """
                SELECT id
                FROM   users
                WHERE  email = 'admin'
            """.trimIndent()
            )
            .fetch()
            .awaitSingle()

        return adminUser["id"] as Long
    }
}