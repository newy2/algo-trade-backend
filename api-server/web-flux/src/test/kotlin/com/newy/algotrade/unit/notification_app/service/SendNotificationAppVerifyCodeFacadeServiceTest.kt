package com.newy.algotrade.unit.notification_app.service

import com.newy.algotrade.common.event.SendNotificationMessageEvent
import com.newy.algotrade.common.exception.VerificationCodeException
import com.newy.algotrade.notification_app.domain.NotificationApp
import com.newy.algotrade.notification_app.domain.Webhook
import com.newy.algotrade.notification_app.port.`in`.model.SendNotificationAppVerifyCodeCommand
import com.newy.algotrade.notification_app.port.out.FindNotificationAppOutPort
import com.newy.algotrade.notification_app.port.out.SaveNotificationAppOutPort
import com.newy.algotrade.notification_app.port.out.SendNotificationMessageOutPort
import com.newy.algotrade.notification_app.service.SendNotificationAppVerifyCodeCommandService
import com.newy.algotrade.notification_app.service.SendNotificationAppVerifyCodeFacadeService
import helpers.spring.MethodAnnotationTestHelper
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@DisplayName("인증코드 처음 요청하기 테스트")
class FirstTrySendNotificationAppVerifyCodeCommandServiceTest : BaseSendNotificationAppVerifyCodeFacadeServiceTest() {
    private val command = SendNotificationAppVerifyCodeCommand(
        userId = 1,
        webhookType = "SLACK",
        webhookUrl = "https://hooks.slack.com/services/1111",
    )

    @Test
    fun `인증코드를 생성에 성공하면 SaveNotificationAppOutPort 를 호출한다`() = runTest {
        var savedVerifyCode: String? = null
        val captureAdapter = SaveNotificationAppOutPort { app ->
            true.also {
                savedVerifyCode = app.verifyCode
            }
        }
        val service = createFacadeService(
            commandService = createService(
                saveNotificationAppOutPort = captureAdapter,
            )
        )

        val verifyCode = service.saveAndSendVerifyCode(command)

        assertEquals(5, savedVerifyCode?.length)
        assertEquals(verifyCode, savedVerifyCode)
    }

    @Test
    fun `인증코드를 생성에 성공하면 sendNotificationMessageOutPort 를 호출한다`() = runTest {
        var event: SendNotificationMessageEvent? = null
        val captureAdapter = SendNotificationMessageOutPort {
            event = it
        }
        val service = createFacadeService(
            commandService = createService(
                sendNotificationMessageOutPort = captureAdapter,
            ),
        )

        val verifyCode = service.saveAndSendVerifyCode(command)

        assertEquals(1, event?.userId)
        assertEquals("인증코드: $verifyCode", event?.message)
        assertEquals(false, event?.isVerified)
    }
}

@DisplayName("인증코드 재요청하기 테스트")
class RetrySendNotificationAppVerifyCodeCommandServiceTest : BaseSendNotificationAppVerifyCodeFacadeServiceTest() {
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
    fun `인증코드를 재요청하면 기존에 사용한 verifyCode 와 다른 verifyCode 를 생성한다`() = runTest {
        val foundSavedAppAdapter = FindNotificationAppOutPort {
            savedNotificationApp.copy(verifyCode = "A1B2C")
        }
        val service = createFacadeService(
            commandService = createService(
                findNotificationAppOutPort = foundSavedAppAdapter,
            )
        )

        val newVerifyCode = service.saveAndSendVerifyCode(command)

        assertEquals(5, newVerifyCode.length)
        assertNotEquals("A1B2C", newVerifyCode)
    }

    @Test
    fun `검증이 완료된 알림앱으로 인증코드를 재요청하면 에러가 발생한다`() = runTest {
        val foundVerifiedAppAdapter = FindNotificationAppOutPort {
            savedNotificationApp.copy(isVerified = true)
        }
        val service = createFacadeService(
            commandService = createService(
                findNotificationAppOutPort = foundVerifiedAppAdapter,
            )
        )

        val error = assertThrows<VerificationCodeException> {
            service.saveAndSendVerifyCode(command)
        }
        assertEquals("이미 검증 완료된 Webhook 입니다.", error.message)
    }

    @Test
    fun `검증 진행중인 Webhook URL 과 다른 URL로 인증코드를 재요청하는 경우 에러가 발생한다`() = runTest {
        val foundDifferentWebhookAdapter = FindNotificationAppOutPort {
            savedNotificationApp.copy(
                webhook = Webhook(
                    type = "SLACK",
                    url = "https://hooks.slack.com/services/1111",
                )
            )
        }
        val service = createFacadeService(
            commandService = createService(
                findNotificationAppOutPort = foundDifferentWebhookAdapter,
            )
        )

        val error = assertThrows<VerificationCodeException> {
            service.saveAndSendVerifyCode(
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

open class BaseSendNotificationAppVerifyCodeFacadeServiceTest {
    protected fun createFacadeService(
        commandService: SendNotificationAppVerifyCodeCommandService = createService()
    ) = SendNotificationAppVerifyCodeFacadeService(commandService)

    protected fun createService(
        findNotificationAppOutPort: FindNotificationAppOutPort = FindNotificationAppOutPort { null },
        saveNotificationAppOutPort: SaveNotificationAppOutPort = SaveNotificationAppOutPort { true },
        sendNotificationMessageOutPort: SendNotificationMessageOutPort = SendNotificationMessageOutPort {},
    ) = SendNotificationAppVerifyCodeCommandService(
        findNotificationAppOutPort,
        saveNotificationAppOutPort,
        sendNotificationMessageOutPort,
    )
}


class SendNotificationAppVerifyCodeCommandServiceAnnotationTest {
    @Test
    fun `메서드 애너테이션 사용 여부 확인`() {
        assertTrue(MethodAnnotationTestHelper(SendNotificationAppVerifyCodeFacadeService::saveAndSendVerifyCode).hasNotTransactionalAnnotation())

        assertTrue(MethodAnnotationTestHelper(SendNotificationAppVerifyCodeCommandService::saveVerifyCode).hasWritableTransactionalAnnotation())
        assertTrue(MethodAnnotationTestHelper(SendNotificationAppVerifyCodeCommandService::sendVerifyCode).hasNotTransactionalAnnotation())
    }
}