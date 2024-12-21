package com.newy.algotrade.integration.notification.adapter.out.persistence

import com.newy.algotrade.common.domain.consts.NotificationAppType
import com.newy.algotrade.common.domain.consts.SendNotificationLogStatus
import com.newy.algotrade.notification.domain.SendNotificationLog
import com.newy.algotrade.web_flux.notification.adapter.out.persistence.SendNotificationLogAdapter
import com.newy.algotrade.web_flux.notification.adapter.out.persistence.repository.NotificationAppR2dbcEntity
import com.newy.algotrade.web_flux.notification.adapter.out.persistence.repository.NotificationAppRepository
import com.newy.algotrade.web_flux.notification.adapter.out.persistence.repository.SendNotificationLogRepository
import helpers.BaseDbTest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitSingle
import kotlin.properties.Delegates

@DisplayName("send_notification_log 생성 테스트")
class CreateByStatusRequestedTest(
    @Autowired private val adapter: SendNotificationLogAdapter,
) : BaseSendNotificationLogAdapterTest() {
    @Test
    fun `REQUESTED 상태로 생성하기`() = runTransactional {
        val notificationAppId = setNotificationAppId()
        val sendNotificationLogId = let {
            adapter.saveSendNotificationLog(notificationAppId, requestMessage = "not used(for ID test)")
            adapter.saveSendNotificationLog(notificationAppId, requestMessage = "not used(for ID test)")
            adapter.saveSendNotificationLog(notificationAppId, requestMessage = "message")
        }

        assertNotEquals(notificationAppId, sendNotificationLogId)
        assertEquals(
            SendNotificationLog(
                sendNotificationLogId = sendNotificationLogId,
                notificationAppId = notificationAppId,
                notificationAppType = NotificationAppType.SLACK,
                status = SendNotificationLogStatus.REQUESTED,
                url = "url",
                requestMessage = "message",
            ),
            adapter.findSendNotificationLog(sendNotificationLogId)
        )
    }
}

@DisplayName("send_notification_log 테이블 업데이트 테스트")
class SaveSendNotificationLogTest(
    @Autowired private val repository: SendNotificationLogRepository,
    @Autowired private val adapter: SendNotificationLogAdapter,
) : BaseSendNotificationLogAdapterTest() {
    private var notificationAppId by Delegates.notNull<Long>()
    private var sendNotificationLogId by Delegates.notNull<Long>()
    private var domainEntity by Delegates.notNull<SendNotificationLog>()
    private var expected by Delegates.notNull<SendNotificationLog>()

    @BeforeEach
    fun setUp(): Unit = runBlocking {
        notificationAppId = setNotificationAppId()
        sendNotificationLogId = let {
            adapter.saveSendNotificationLog(notificationAppId, requestMessage = "not used(for ID test)")
            adapter.saveSendNotificationLog(notificationAppId, requestMessage = "not used(for ID test)")
            adapter.saveSendNotificationLog(notificationAppId, requestMessage = "message")
        }
        assertNotEquals(notificationAppId, sendNotificationLogId)

        domainEntity = adapter.findSendNotificationLog(sendNotificationLogId)!!
        expected = SendNotificationLog(
            sendNotificationLogId = sendNotificationLogId,
            notificationAppId = notificationAppId,
            notificationAppType = NotificationAppType.SLACK,
            status = SendNotificationLogStatus.REQUESTED,
            url = "url",
            requestMessage = "message",
        )
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        deleteNotificationAppId(notificationAppId)
        repository.deleteAll()
    }

    @Test
    fun `PROSSESING 상태로 변경하기`() = runBlocking {
        adapter.saveSendNotificationLog(domainEntity.statusProcessing())

        assertEquals(
            expected.copy(status = SendNotificationLogStatus.PROCESSING),
            adapter.findSendNotificationLog(sendNotificationLogId)
        )
    }

    @Test
    fun `성공 응답 메세지 업데이트 하기`() = runBlocking {
        val successMessage = "ok"
        adapter.saveSendNotificationLog(domainEntity.statusProcessing().responseMessage(successMessage))

        assertEquals(
            expected.copy(
                status = SendNotificationLogStatus.SUCCEED,
                responseMessage = successMessage,
            ),
            adapter.findSendNotificationLog(sendNotificationLogId)
        )
    }

    @Test
    fun `실패 응답 메세지 업데이트 하기`() = runBlocking {
        val notSuccessMessage = "invalid token"
        adapter.saveSendNotificationLog(domainEntity.statusProcessing().responseMessage(notSuccessMessage))

        assertEquals(
            expected.copy(
                status = SendNotificationLogStatus.FAILED,
                responseMessage = notSuccessMessage,
            ),
            adapter.findSendNotificationLog(sendNotificationLogId)
        )
    }
}

open class BaseSendNotificationLogAdapterTest : BaseDbTest() {
    @Autowired
    private lateinit var databaseClient: DatabaseClient

    @Autowired
    private lateinit var notificationAppRepository: NotificationAppRepository

    protected suspend fun setNotificationAppId(): Long =
        notificationAppRepository.save(
            NotificationAppR2dbcEntity(
                userId = getAdminUserId(),
                type = "SLACK",
                url = "url"
            )
        ).id

    protected suspend fun deleteNotificationAppId(notificationAppId: Long) =
        notificationAppRepository.deleteById(notificationAppId)

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