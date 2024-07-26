package com.newy.algotrade.integration.notification.adapter.out.persistent

import com.newy.algotrade.domain.common.exception.NotFoundRowException
import com.newy.algotrade.domain.common.exception.PreconditionError
import com.newy.algotrade.web_flux.notification.adapter.out.persistent.SendNotificationCommandAdapter
import com.newy.algotrade.web_flux.notification.adapter.out.persistent.repository.NotificationAppEntity
import com.newy.algotrade.web_flux.notification.adapter.out.persistent.repository.NotificationAppRepository
import com.newy.algotrade.web_flux.notification.adapter.out.persistent.repository.SendNotificationLogEntity
import com.newy.algotrade.web_flux.notification.adapter.out.persistent.repository.SendNotificationLogEntity.Status
import com.newy.algotrade.web_flux.notification.adapter.out.persistent.repository.SendNotificationLogRepository
import helpers.BaseDbTest
import kotlinx.coroutines.flow.toList
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

@DisplayName("'요청중', '처리중' 상태로 변경하는 테스트")
class SetStatusRequestedAndPutStatusProcessingTest(
    @Autowired private val repository: SendNotificationLogRepository,
    @Autowired private val adapter: SendNotificationCommandAdapter,
) : BaseSendNotificationAdapterTest() {
    private var notificationAppId by Delegates.notNull<Long>()
    private var sendNotificationLogId by Delegates.notNull<Long>()

    @BeforeEach
    fun setUp(): Unit = runBlocking {
        notificationAppId = setNotificationAppId()
        sendNotificationLogId = adapter.setStatusRequested(notificationAppId, requestMessage = "message")
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        deleteNotificationAppId(notificationAppId)
        repository.deleteById(sendNotificationLogId)
    }

    @Test
    fun `setStatusRequested - '요청중' 상태로 생성하기`() = runBlocking {
        val savedList = repository.findAll().toList()
        assertEquals(1, savedList.size)
        savedList.first().let {
            assertEquals(sendNotificationLogId, it.id)
            assertEquals("message", it.requestMessage)
            assertEquals(null, it.responseMessage)
            assertEquals(Status.REQUESTED.name, it.status)
        }
    }

    @Test
    fun `putStatusProcessing - '처리중' 상태로 업데이트 하기`() = runBlocking {
        val isUpdated = adapter.putStatusProcessing(sendNotificationLogId)
        assertTrue(isUpdated)

        val savedList = repository.findAll().toList()
        assertEquals(1, savedList.size)
        savedList.first().let {
            assertEquals(sendNotificationLogId, it.id)
            assertEquals("message", it.requestMessage)
            assertEquals(null, it.responseMessage)
            assertEquals(Status.PROCESSING.name, it.status)
        }
    }
}

@DisplayName("알림 전송 응답 메세지 저장 테스트")
class PutResponseMessageTest(
    @Autowired private val repository: SendNotificationLogRepository,
    @Autowired private val adapter: SendNotificationCommandAdapter,
) : BaseSendNotificationAdapterTest() {
    private var notificationAppId by Delegates.notNull<Long>()
    private var sendNotificationLogId by Delegates.notNull<Long>()

    @BeforeEach
    fun setUp(): Unit = runBlocking {
        notificationAppId = setNotificationAppId()
        sendNotificationLogId = adapter.setStatusRequested(notificationAppId, requestMessage = "message")
        adapter.putStatusProcessing(sendNotificationLogId)
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        deleteNotificationAppId(notificationAppId)
        repository.deleteById(sendNotificationLogId)
    }

    @Test
    fun `성공 응답 메세지 업데이트 하기`() = runBlocking {
        val successMessage = "ok"
        adapter.putResponseMessage(sendNotificationLogId, responseMessage = successMessage)

        val savedList = repository.findAll().toList()
        assertEquals(1, savedList.size)
        savedList.first().let {
            assertEquals(sendNotificationLogId, it.id)
            assertEquals("message", it.requestMessage)
            assertEquals("ok", it.responseMessage)
            assertEquals(Status.SUCCEED.name, it.status)
        }
    }

    @Test
    fun `에러 응답 메세지 업데이트 하기`() = runBlocking {
        val notSuccessMessage = "INVALID TOKEN"
        adapter.putResponseMessage(sendNotificationLogId, responseMessage = notSuccessMessage)

        val savedList = repository.findAll().toList()
        assertEquals(1, savedList.size)
        savedList.first().let {
            assertEquals(sendNotificationLogId, it.id)
            assertEquals("message", it.requestMessage)
            assertEquals("INVALID TOKEN", it.responseMessage)
            assertEquals(Status.FAILED.name, it.status)
        }
    }
}

@DisplayName("저장되지 않은 ID 로 adapter 를 사용하는 경우에 대한 테스트")
class NotFoundRowExceptionTest(
    @Autowired private val repository: SendNotificationLogRepository,
    @Autowired private val adapter: SendNotificationCommandAdapter,
) : BaseDbTest() {
    private val unSavedSendNotificationLogId: Long = 100

    @BeforeEach
    fun setUp(): Unit = runBlocking {
        assertNull(repository.findById(unSavedSendNotificationLogId))
    }

    @Test
    fun `putStatusProcessing - 저장되지 않은 ID 로 호출한 경우`() = runBlocking {
        try {
            adapter.putStatusProcessing(unSavedSendNotificationLogId)
            fail()
        } catch (e: NotFoundRowException) {
            assertTrue(true)
        }
    }


    @Test
    fun `putResponseMessage - 저장되지 않은 ID 로 호출한 경우`() = runBlocking {
        try {
            adapter.putResponseMessage(unSavedSendNotificationLogId, responseMessage = "ok")
            fail()
        } catch (e: NotFoundRowException) {
            assertTrue(true)
        }
    }
}

@DisplayName("status 변경하기 전 상태 확인하기")
class StatusErrorTest(
    @Autowired private val repository: SendNotificationLogRepository,
    @Autowired private val adapter: SendNotificationCommandAdapter,
) : BaseSendNotificationAdapterTest() {
    private var notificationAppId by Delegates.notNull<Long>()
    private var statusMap by Delegates.notNull<MutableMap<Status, SendNotificationLogEntity>>()

    @BeforeEach
    fun setUp(): Unit = runBlocking {
        notificationAppId = setNotificationAppId()
        statusMap = mutableMapOf<Status, SendNotificationLogEntity>().also {
            Status.values().forEach { status ->
                it[status] = repository.save(
                    SendNotificationLogEntity(
                        notificationAppId = notificationAppId,
                        status = status.name
                    )
                )
            }
        }
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        deleteNotificationAppId(notificationAppId)
        repository.deleteAll()
    }


    @Test
    fun `putStatusProcessing - 이전 상태가 REQUESTED, FAILED 가 아닌 경우`() = runBlocking {
        for ((status, sendNotificationLogEntity) in statusMap) {
            val preconditionStatuses = listOf(Status.REQUESTED, Status.FAILED)
            if (preconditionStatuses.contains(status)) {
                continue
            }

            try {
                adapter.putStatusProcessing(sendNotificationLogEntity.id)
                fail()
            } catch (e: PreconditionError) {
                assertTrue(true)
            }
        }
    }


    @Test
    fun `putResponseMessage - 이전 상태가 PROCESSING 가 아닌 경우`() = runBlocking {
        for ((status, sendNotificationLogEntity) in statusMap) {
            val preconditionStatuses = listOf(Status.PROCESSING)
            if (preconditionStatuses.contains(status)) {
                continue
            }

            try {
                adapter.putResponseMessage(sendNotificationLogEntity.id, responseMessage = "ok")
                fail()
            } catch (e: PreconditionError) {
                assertTrue(true)
            }
        }
    }
}


open class BaseSendNotificationAdapterTest : BaseDbTest() {
    @Autowired
    private lateinit var databaseClient: DatabaseClient

    @Autowired
    private lateinit var notificationAppRepository: NotificationAppRepository

    protected suspend fun setNotificationAppId(): Long =
        notificationAppRepository.save(
            NotificationAppEntity(
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