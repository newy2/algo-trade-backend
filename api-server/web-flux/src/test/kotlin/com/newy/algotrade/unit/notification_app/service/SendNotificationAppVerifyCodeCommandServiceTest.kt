package com.newy.algotrade.unit.notification_app.service

import com.newy.algotrade.common.event.SendNotificationMessageEvent
import com.newy.algotrade.common.exception.InitializedError
import com.newy.algotrade.notification_app.domain.NotificationApp
import com.newy.algotrade.notification_app.domain.Webhook
import com.newy.algotrade.notification_app.port.`in`.model.SendNotificationAppVerifyCodeCommand
import com.newy.algotrade.notification_app.port.out.FindNotificationAppOutPort
import com.newy.algotrade.notification_app.port.out.SaveNotificationAppOutPort
import com.newy.algotrade.notification_app.port.out.SendNotificationMessageOutPort
import com.newy.algotrade.notification_app.service.SendNotificationAppVerifyCodeCommandService
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class NullFindNotificationAppOutPort : FindNotificationAppOutPort {
    override suspend fun findByUserId(userId: Long) = null
}

class NullSaveNotificationAppOutPort : SaveNotificationAppOutPort {
    override suspend fun save(app: NotificationApp) = false
}

class NullSendNotificationAppOutPort : SendNotificationMessageOutPort {
    override suspend fun send(event: SendNotificationMessageEvent) {}
}


@DisplayName("인증코드 요청하기 테스트")
class FirstTrySendNotificationAppVerifyCodeCommandServiceTest {
    private val command = SendNotificationAppVerifyCodeCommand(
        userId = 1,
        webhookType = "SLACK",
        webhookUrl = "https://hooks.slack.com/services/1111",
    )

    @Test
    fun `인증코드를 생성에 성공하면 SaveNotificationAppOutPort 를 호출한다`() = runTest {
        var savedVerifyCode = ""
        val mockSaveNotificationAppOutPort = SaveNotificationAppOutPort { app ->
            true.also {
                savedVerifyCode = app.verifyCode
            }
        }
        val service = SendNotificationAppVerifyCodeCommandService(
            findNotificationAppOutPort = NullFindNotificationAppOutPort(),
            saveNotificationAppOutPort = mockSaveNotificationAppOutPort,
            sendNotificationMessageOutPort = NullSendNotificationAppOutPort(),
        )

        val verifyCode = service.sendVerifyCode(command)

        assertEquals(5, savedVerifyCode.length)
        assertEquals(verifyCode, savedVerifyCode)
    }

    @Test
    fun `인증코드를 생성에 성공하면 sendNotificationMessageOutPort 를 호출한다`() = runTest {
        var event: SendNotificationMessageEvent? = null
        val mockSendNotificationMessageOutPort = SendNotificationMessageOutPort {
            event = it
        }
        val service = SendNotificationAppVerifyCodeCommandService(
            findNotificationAppOutPort = NullFindNotificationAppOutPort(),
            saveNotificationAppOutPort = NullSaveNotificationAppOutPort(),
            sendNotificationMessageOutPort = mockSendNotificationMessageOutPort,
        )

        val verifyCode = service.sendVerifyCode(command)

        assertEquals(1, event?.userId)
        assertEquals("인증코드: $verifyCode", event?.message)
        assertEquals(false, event?.isVerified)
    }
}

@DisplayName("인증코드 재요청하기 테스트")
class RetrySendNotificationAppVerifyCodeCommandServiceTest {
    private val savedNotificationApp = NotificationApp(
        userId = 1,
        webhook = Webhook(
            type = "SLACK",
            url = "https://hooks.slack.com/services/1111",
        ),
        isVerified = false,
    )
    private val command = SendNotificationAppVerifyCodeCommand(
        userId = 1,
        webhookType = "SLACK",
        webhookUrl = "https://hooks.slack.com/services/1111",
    )

    @Test
    fun `인증코드 재요청하기`() = runTest {
        val beforeUsedVerifyCode = "A1B2C"
        val alreadySavedNotificationAppOutPort = FindNotificationAppOutPort {
            savedNotificationApp.copy(verifyCode = beforeUsedVerifyCode)
        }
        val service = SendNotificationAppVerifyCodeCommandService(
            findNotificationAppOutPort = alreadySavedNotificationAppOutPort,
            saveNotificationAppOutPort = NullSaveNotificationAppOutPort(),
            sendNotificationMessageOutPort = NullSendNotificationAppOutPort(),
        )

        val newVerifyCode = service.sendVerifyCode(command)

        assertEquals(5, newVerifyCode.length)
        assertNotEquals(beforeUsedVerifyCode, newVerifyCode)
    }

    @Test
    fun `검증 완료된 알림앱으로 인증코드를 재요청하는 경우 에러가 발생한다`() = runTest {
        val alreadyVerifiedNotificationAppOutPort = FindNotificationAppOutPort {
            savedNotificationApp.copy(isVerified = true)
        }
        val service = SendNotificationAppVerifyCodeCommandService(
            findNotificationAppOutPort = alreadyVerifiedNotificationAppOutPort,
            saveNotificationAppOutPort = NullSaveNotificationAppOutPort(),
            sendNotificationMessageOutPort = NullSendNotificationAppOutPort(),
        )

        val error = assertThrows<InitializedError> {
            service.sendVerifyCode(command)
        }
        assertEquals("이미 검증 완료된 Webhook 입니다.", error.message)
    }

    @Test
    fun `검증 진행중인 Webhook URL 과 다른 URL로 인증코드를 재요청하는 경우 에러가 발생한다`() = runTest {
        val alreadySavedNotificationAppOutPort = FindNotificationAppOutPort {
            savedNotificationApp.copy(
                webhook = Webhook(
                    type = "SLACK",
                    url = "https://hooks.slack.com/services/1111",
                )
            )
        }
        val service = SendNotificationAppVerifyCodeCommandService(
            findNotificationAppOutPort = alreadySavedNotificationAppOutPort,
            saveNotificationAppOutPort = NullSaveNotificationAppOutPort(),
            sendNotificationMessageOutPort = NullSendNotificationAppOutPort(),
        )

        val error = assertThrows<InitializedError> {
            service.sendVerifyCode(
                command.copy(
                    webhookUrl = "https://hooks.slack.com/services/2222"
                )
            )
        }
        assertEquals(
            "기존 Webhook URL 과 다릅니다. (https://hooks.slack.com/services/1111 != https://hooks.slack.com/services/2222)",
            error.message
        )
    }
}