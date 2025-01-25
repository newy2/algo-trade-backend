package com.newy.algotrade.unit.notification.port.`in`.model

import com.newy.algotrade.notification.port.`in`.model.SendNotificationMessageCommand
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class SendNotificationMessageCommandTest {
    private val inPortModel = SendNotificationMessageCommand(
        userId = 1,
        message = "test message",
        isVerified = true,
    )

    @Test
    fun `userId 는 0 이상이어야 한다`() {
        assertThrows<ConstraintViolationException> { inPortModel.copy(userId = -1) }
        assertThrows<ConstraintViolationException> { inPortModel.copy(userId = 0) }
        assertDoesNotThrow {
            inPortModel.copy(userId = 1)
            inPortModel.copy(userId = 2)
        }
    }

    @Test
    fun `message 는 빈 문자열일 수 없다`() {
        assertThrows<ConstraintViolationException> {
            inPortModel.copy(message = "")
        }

        assertThrows<ConstraintViolationException> {
            inPortModel.copy(message = " ")
        }

        assertDoesNotThrow {
            inPortModel.copy(message = "message")
        }
    }
}