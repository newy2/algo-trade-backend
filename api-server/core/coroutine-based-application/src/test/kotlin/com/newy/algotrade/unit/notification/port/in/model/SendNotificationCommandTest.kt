package com.newy.algotrade.unit.notification.port.`in`.model

import com.newy.algotrade.coroutine_based_application.notification.port.`in`.model.SendNotificationCommand
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class SendNotificationCommandTest {
    private val dto = SendNotificationCommand(
        notificationAppId = 1,
        requestMessage = "request message",
    )

    @Test
    fun `notificationAppId 는 0 이상이어야 함`() {
        assertThrows<ConstraintViolationException> { dto.copy(notificationAppId = -1) }
        assertThrows<ConstraintViolationException> { dto.copy(notificationAppId = 0) }
        assertDoesNotThrow {
            dto.copy(notificationAppId = 1)
            dto.copy(notificationAppId = 2)
        }
    }

    @Test
    fun `message 는 빈 문자열이연 안됨`() {
        assertThrows<ConstraintViolationException> {
            dto.copy(requestMessage = "")
        }
        assertThrows<ConstraintViolationException> {
            dto.copy(requestMessage = " ")
        }
        assertDoesNotThrow {
            dto.copy(requestMessage = "a")
        }
    }
}