package com.newy.algotrade.unit.notification.adapter.`in`.web

import com.newy.algotrade.coroutine_based_application.notification.port.`in`.NotificationAppUseCase
import com.newy.algotrade.coroutine_based_application.notification.port.`in`.model.SetNotificationAppCommand
import com.newy.algotrade.domain.common.consts.GlobalEnv
import com.newy.algotrade.domain.common.consts.NotificationAppType
import com.newy.algotrade.web_flux.notification.adapter.`in`.web.SetNotificationAppController
import com.newy.algotrade.web_flux.notification.adapter.`in`.web.model.SetNotificationAppRequest
import helpers.TestEnv
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class NotificationAppControllerTest : NotificationAppUseCase {
    private lateinit var incomingPortModel: SetNotificationAppCommand

    @Test
    fun `request 모델을 command 모델로 변경하기`() = runBlocking {
        GlobalEnv.initializeAdminUserId(TestEnv.TEST_ADMIN_USER_ID)

        val controller = SetNotificationAppController(this@NotificationAppControllerTest)

        controller.setNotificationApp(
            SetNotificationAppRequest(
                type = "SLACK",
                url = NotificationAppType.SLACK.host + "/XXX/YYY"
            )
        )

        Assertions.assertEquals(
            SetNotificationAppCommand(
                userId = TestEnv.TEST_ADMIN_USER_ID,
                type = NotificationAppType.SLACK,
                url = NotificationAppType.SLACK.host + "/XXX/YYY"
            ),
            incomingPortModel
        )
    }

    override suspend fun setNotificationApp(command: SetNotificationAppCommand): Boolean {
        incomingPortModel = command
        return true
    }
}