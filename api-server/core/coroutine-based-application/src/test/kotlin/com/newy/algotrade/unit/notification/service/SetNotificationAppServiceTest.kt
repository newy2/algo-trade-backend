package com.newy.algotrade.unit.notification.service

import com.newy.algotrade.coroutine_based_application.notification.port.`in`.model.SetNotificationAppCommand
import com.newy.algotrade.coroutine_based_application.notification.port.out.HasNotificationAppPort
import com.newy.algotrade.coroutine_based_application.notification.port.out.SetNotificationAppPort
import com.newy.algotrade.coroutine_based_application.notification.service.SetNotificationAppService
import com.newy.algotrade.domain.common.consts.NotificationApp
import com.newy.algotrade.domain.common.exception.DuplicateDataException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.fail

open class NoErrorNotificationAppAdapter : HasNotificationAppPort, SetNotificationAppPort {
    override suspend fun hasNotificationApp(userId: Long) = false
    override suspend fun setNotificationApp(command: SetNotificationAppCommand) = true
}

class FailedSetNotificationAppServiceTest {
    @Test
    fun `이미 알림 앱을 등록한 경우`() = runTest {
        val alreadyExistsAdapter = object : HasNotificationAppPort {
            override suspend fun hasNotificationApp(userId: Long) = true
        }

        val service = SetNotificationAppService(
            hasNotificationAppPort = alreadyExistsAdapter,
            setNotificationAppPort = NoErrorNotificationAppAdapter()
        )

        val command = SetNotificationAppCommand(
            userId = 1,
            type = NotificationApp.SLACK,
            url = NotificationApp.SLACK.baseUrl
        )

        try {
            service.setNotificationApp(command)
            fail()
        } catch (exception: DuplicateDataException) {
            assertTrue(true)
        }
    }
}

class SuccessSetNotificationAppServiceTest : NoErrorNotificationAppAdapter() {
    private var log = ""

    @Test
    fun test() = runTest {
        val service = SetNotificationAppService(
            hasNotificationAppPort = this@SuccessSetNotificationAppServiceTest,
            setNotificationAppPort = this@SuccessSetNotificationAppServiceTest
        )

        val command = SetNotificationAppCommand(
            userId = 1,
            type = NotificationApp.SLACK,
            url = NotificationApp.SLACK.baseUrl
        )

        val isRegistered = service.setNotificationApp(command)

        assertTrue(isRegistered)
        assertEquals("hasNotificationAppPort setNotificationAppPort ", log)
    }

    override suspend fun hasNotificationApp(userId: Long): Boolean {
        log += "hasNotificationAppPort "
        return super.hasNotificationApp(userId)
    }

    override suspend fun setNotificationApp(command: SetNotificationAppCommand): Boolean {
        log += "setNotificationAppPort "
        return super.setNotificationApp(command)
    }
}