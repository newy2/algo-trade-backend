package com.newy.algotrade.unit.notification.port.`in`.model

import com.newy.algotrade.coroutine_based_application.notification.port.`in`.model.SetNotificationAppCommand
import com.newy.algotrade.domain.common.consts.NotificationApp
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows


val dto = SetNotificationAppCommand(
    userId = 1,
    type = NotificationApp.SLACK,
    url = "https://hooks.slack.com/services/XXX/YYY",
)

class SetNotificationAppCommandTest {
    @Test
    fun `userId 는 0 이상이어야 함`() {
        assertThrows<ConstraintViolationException> { dto.copy(userId = -1) }
        assertThrows<ConstraintViolationException> { dto.copy(userId = 0) }
        assertDoesNotThrow {
            dto.copy(userId = 1)
            dto.copy(userId = 2)
        }
    }

    @Test
    fun productCategory() {
        assertThrows<IllegalArgumentException> {
            dto.copy(type = NotificationApp.valueOf("NOT_REGISTERED_NAME"))
        }
    }

    @Test
    fun `productCategory 값이 'SLACK' 인 경우, webhook URL 패턴이어야 한다`() {
        assertThrows<ConstraintViolationException> {
            dto.copy(
                type = NotificationApp.SLACK,
                url = ""
            )
        }
        assertThrows<IllegalArgumentException>("올바르지 못한 URL") {
            dto.copy(
                type = NotificationApp.SLACK,
                url = "https://naver.com"
            )
        }
        assertThrows<IllegalArgumentException>("올바르지 못한 URL2") {
            dto.copy(
                type = NotificationApp.SLACK,
                url = "https://hooks.slack.com"
            )
        }
        assertDoesNotThrow {
            dto.copy(
                type = NotificationApp.SLACK,
                url = "https://hooks.slack.com/services/XXX/YYY"
            )
        }
    }
}