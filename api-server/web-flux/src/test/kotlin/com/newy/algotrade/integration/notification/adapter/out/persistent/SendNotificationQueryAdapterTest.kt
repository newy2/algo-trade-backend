package com.newy.algotrade.integration.notification.adapter.out.persistent

import com.newy.algotrade.coroutine_based_application.notification.domain.SendNotification
import com.newy.algotrade.domain.common.consts.NotificationApp
import com.newy.algotrade.domain.common.exception.NotFoundRowException
import com.newy.algotrade.web_flux.notification.adapter.out.persistent.SendNotificationQueryAdapter
import com.newy.algotrade.web_flux.notification.adapter.out.persistent.repository.NotificationAppEntity
import com.newy.algotrade.web_flux.notification.adapter.out.persistent.repository.NotificationAppRepository
import com.newy.algotrade.web_flux.notification.adapter.out.persistent.repository.SendNotificationLogEntity
import com.newy.algotrade.web_flux.notification.adapter.out.persistent.repository.SendNotificationLogEntity.Status
import com.newy.algotrade.web_flux.notification.adapter.out.persistent.repository.SendNotificationLogRepository
import helpers.BaseDbTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitSingle

class SendNotificationQueryAdapterTest(
    @Autowired private val databaseClient: DatabaseClient,
    @Autowired private val notificationAppRepository: NotificationAppRepository,
    @Autowired private val sendNotificationLogRepository: SendNotificationLogRepository,

    @Autowired private val adapter: SendNotificationQueryAdapter,
) : BaseDbTest() {

    @Test
    fun `저장되지 않은 ID 로 조회하는 경우`() = runTransactional {
        val unSavedId: Long = 100
        assertNull(sendNotificationLogRepository.findById(unSavedId))

        try {
            adapter.getSendNotification(unSavedId)
            fail()
        } catch (e: NotFoundRowException) {
            assertTrue(true)
        }
    }

    @Test
    fun `저장된 ID 로 조회하는 경우`() = runTransactional {
        val sendNotificationLogId = setSendNotificationLogId(
            type = "SLACK",
            url = "url",
            requestMessage = "message"
        )

        assertEquals(
            SendNotification(
                notificationApp = NotificationApp.SLACK,
                url = "url",
                requestMessage = "message"
            ),
            adapter.getSendNotification(sendNotificationLogId)
        )
    }

    private suspend fun setSendNotificationLogId(
        type: String,
        url: String,
        requestMessage: String,
    ): Long =
        notificationAppRepository.save(
            NotificationAppEntity(
                userId = getAdminUserId(),
                type = type,
                url = url
            )
        ).let {
            sendNotificationLogRepository.save(
                SendNotificationLogEntity(
                    notificationAppId = it.id,
                    requestMessage = requestMessage,
                    status = Status.REQUESTED.name
                )
            ).id
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