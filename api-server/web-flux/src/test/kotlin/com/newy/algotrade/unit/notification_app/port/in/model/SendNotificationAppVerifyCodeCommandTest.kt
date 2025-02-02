package com.newy.algotrade.unit.notification_app.port.`in`.model

import com.newy.algotrade.notification_app.domain.NotificationApp
import com.newy.algotrade.notification_app.domain.Webhook
import com.newy.algotrade.notification_app.port.`in`.model.SendNotificationAppVerifyCodeCommand
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import kotlin.test.assertEquals

class SendNotificationAppVerifyCodeCommandTest {
    private val inPortModel = SendNotificationAppVerifyCodeCommand(
        userId = 1,
        webhookType = "SLACK",
        webhookUrl = "https://hooks.slack.com/services/XXX/YYY",
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
    fun `webhookType 은 'SLACK' 만 지원한다`() {
        assertThrows<ConstraintViolationException> {
            inPortModel.copy(webhookType = "DISCODE")
        }
        assertDoesNotThrow {
            inPortModel.copy(webhookType = "SLACK")
        }
    }

    @Test
    fun `webhookUrl 은 정해진 host 를 prefix 로 사용해야 한다`() {
        assertThrows<ConstraintViolationException> {
            inPortModel.copy(webhookUrl = "https://slack.com/services/XXX/YYY")
        }
        assertThrows<ConstraintViolationException> {
            inPortModel.copy(webhookUrl = "https://hooks.slack.com/XXX/YYY")
        }
        assertThrows<ConstraintViolationException> {
            inPortModel.copy(webhookUrl = "http://hooks.slack.com/services/XXX/YYY")
        }
        assertDoesNotThrow {
            inPortModel.copy(webhookUrl = "https://hooks.slack.com/services/XXX/YYY")
        }
    }

    @Test
    fun `InPort 모델은 도메인 모델로 매핑할 수 있어야 한다`() {
        val expiredAt: LocalDateTime = NotificationApp.getDefaultExpiredAt()
        assertEquals(
            NotificationApp(
                userId = 1,
                webhook = Webhook(
                    type = "SLACK",
                    url = "https://hooks.slack.com/services/XXX/YYY",
                ),
                expiredAt = expiredAt,
            ),
            inPortModel.toDomainModel(expiredAt)
        )
    }
}