package com.newy.algotrade.unit.notification_app.port.`in`.model

import com.newy.algotrade.notification_app.port.`in`.model.DeleteNotificationAppCommand
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class DeleteNotificationAppCommandTest {
    private val inPortModel = DeleteNotificationAppCommand(
        userId = 1,
        notificationAppId = 2
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
    fun `notificationAppId 는 0 이상이어야 한다`() {
        assertThrows<ConstraintViolationException> { inPortModel.copy(notificationAppId = -1) }
        assertThrows<ConstraintViolationException> { inPortModel.copy(notificationAppId = 0) }
        assertDoesNotThrow {
            inPortModel.copy(notificationAppId = 1)
            inPortModel.copy(notificationAppId = 2)
        }
    }
}