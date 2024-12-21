package com.newy.algotrade.unit.notification.port.`in`.model

import com.newy.algotrade.common.domain.consts.NotificationAppType
import com.newy.algotrade.notification.domain.NotificationApp
import com.newy.algotrade.notification.port.`in`.model.SetNotificationAppCommand
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class SetNotificationAppCommandTest {
    private val incomingPortModel = SetNotificationAppCommand(
        userId = 1,
        type = NotificationAppType.SLACK,
        url = "https://hooks.slack.com/services/XXX/YYY",
    )

    @Test
    fun `userId 는 0 이상이어야 함`() {
        assertThrows<ConstraintViolationException> { incomingPortModel.copy(userId = -1) }
        assertThrows<ConstraintViolationException> { incomingPortModel.copy(userId = 0) }
        assertDoesNotThrow {
            incomingPortModel.copy(userId = 1)
            incomingPortModel.copy(userId = 2)
        }
    }

    @Test
    fun type() {
        assertThrows<IllegalArgumentException> {
            incomingPortModel.copy(type = NotificationAppType.valueOf("NOT_REGISTERED_NAME"))
        }
    }

    @Test
    fun `productCategory 값이 'SLACK' 인 경우, webhook URL 패턴이어야 한다`() {
        assertThrows<ConstraintViolationException> {
            incomingPortModel.copy(
                type = NotificationAppType.SLACK,
                url = ""
            )
        }
        assertThrows<IllegalArgumentException>("올바르지 못한 URL") {
            incomingPortModel.copy(
                type = NotificationAppType.SLACK,
                url = "https://naver.com"
            )
        }
        assertDoesNotThrow {
            incomingPortModel.copy(
                type = NotificationAppType.SLACK,
                url = "https://hooks.slack.com"
            )
            incomingPortModel.copy(
                type = NotificationAppType.SLACK,
                url = "https://hooks.slack.com/services/XXX/YYY"
            )
        }
    }

    @Test
    fun toDomainEntity() {
        assertEquals(
            NotificationApp(
                id = 0,
                userId = 1,
                type = NotificationAppType.SLACK,
                url = "https://hooks.slack.com/services/XXX/YYY",
            ),
            incomingPortModel.toDomainEntity()
        )
    }
}