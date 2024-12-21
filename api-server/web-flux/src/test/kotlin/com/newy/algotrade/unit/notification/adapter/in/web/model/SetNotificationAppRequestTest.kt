package com.newy.algotrade.unit.notification.adapter.`in`.web.model

import com.newy.algotrade.common.domain.consts.GlobalEnv
import com.newy.algotrade.common.domain.consts.NotificationAppType
import com.newy.algotrade.notification.port.`in`.model.SetNotificationAppCommand
import com.newy.algotrade.web_flux.notification.adapter.`in`.web.model.SetNotificationAppRequest
import helpers.TestEnv
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows


val dto = SetNotificationAppRequest(
    type = "SLACK",
    url = "https://hooks.slack.com/services/XXX/YYY",
)

class SetNotificationAppRequestTest {
    @Test
    fun `type 은 NotificationApp 타입이어야 한다`() {
        assertThrows<ConstraintViolationException> {
            dto.copy(type = "")
        }
        assertThrows<ConstraintViolationException> {
            dto.copy(type = "NOT_REGISTERED_MARKET_NAME")
        }
        assertDoesNotThrow {
            NotificationAppType.values().forEach {
                dto.copy(type = it.name)
            }
        }
    }

    @Test
    fun mapToIncomingPortModel() {
        GlobalEnv.initializeAdminUserId(TestEnv.TEST_ADMIN_USER_ID)

        assertEquals(
            SetNotificationAppCommand(
                userId = TestEnv.TEST_ADMIN_USER_ID,
                type = NotificationAppType.SLACK,
                url = "https://hooks.slack.com/services/XXX/YYY"
            ),
            dto.toIncomingPortModel()
        )
    }
}