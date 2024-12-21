package com.newy.algotrade.unit.notification.adapter.`in`.web

import com.newy.algotrade.common.domain.consts.GlobalEnv
import com.newy.algotrade.common.domain.consts.NotificationAppType
import com.newy.algotrade.notification.adapter.`in`.web.SetNotificationAppController
import com.newy.algotrade.notification.adapter.`in`.web.model.SetNotificationAppRequest
import com.newy.algotrade.notification.port.`in`.model.SetNotificationAppCommand
import helpers.spring.TestEnv
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class NotificationAppControllerTest {
    @Test
    fun `requestModel 을 incomingPortModel 로 변경하기`() = runBlocking {
        GlobalEnv.initializeAdminUserId(TestEnv.TEST_ADMIN_USER_ID)

        var incomingPortModel: SetNotificationAppCommand? = null
        val controller = SetNotificationAppController(
            setNotificationAppUseCase = { command ->
                true.also {
                    incomingPortModel = command
                }
            }
        )

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
}