package com.newy.algotrade.unit.notification_app.service

import com.newy.algotrade.common.exception.VerificationCodeException
import com.newy.algotrade.notification_app.domain.NotificationApp
import com.newy.algotrade.notification_app.domain.Webhook
import com.newy.algotrade.notification_app.port.`in`.model.VerifyNotificationAppCommand
import com.newy.algotrade.notification_app.port.out.FindNotificationAppOutPort
import com.newy.algotrade.notification_app.port.out.SaveNotificationAppOutPort
import com.newy.algotrade.notification_app.service.VerifyNotificationAppCommandService
import helpers.spring.MethodAnnotationTestHelper
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class VerifyNotificationAppCommandServiceTest {
    private val command = VerifyNotificationAppCommand(
        userId = 1,
        verifyCode = "ABCDE",
    )
    private val notificationApp = NotificationApp(
        userId = 1,
        webhook = Webhook(
            type = "SLACK",
            url = "https://hooks.slack.com/services/1111",
        ),
        verifyCode = "ABCDE",
        isVerified = false,
    )

    @Test
    fun `저장된 알림앱을 찾지 못하면 에러를 발생한다`() = runTest {
        val notFoundAdapter = FindNotificationAppOutPort { null }
        val service = createService(
            findNotificationAppOutPort = notFoundAdapter,
        )

        val error = assertThrows<VerificationCodeException> {
            service.verify(command)
        }

        assertEquals("알림 앱을 찾을 수 없습니다. (userId: 1)", error.message)
    }

    @Test
    fun `이미 검증 완료된 경우 에러를 발생한다`() = runTest {
        val foundVerifiedAppAdapter = FindNotificationAppOutPort {
            notificationApp.copy(isVerified = true)
        }
        val service = createService(
            findNotificationAppOutPort = foundVerifiedAppAdapter,
        )

        val error = assertThrows<VerificationCodeException> {
            service.verify(command)
        }

        assertEquals("이미 검증 완료된 Webhook 입니다.", error.message)
    }

    @Test
    fun `verifyCode 가 다르면 에러를 발생한다`() = runTest {
        val foundDifferentVerifyCodeAdapter = FindNotificationAppOutPort {
            notificationApp.copy(verifyCode = "A1B2C")
        }
        val service = createService(
            findNotificationAppOutPort = foundDifferentVerifyCodeAdapter,
        )

        val error = assertThrows<VerificationCodeException> {
            service.verify(command)
        }

        assertEquals("인증 코드가 다릅니다.", error.message)
    }

    @Test
    fun `검증에 성공하면 saveNotificationAppOutPort 를 호출한다`() = runTest {
        var savableNotificationApp: NotificationApp? = null
        val captureAdapter = SaveNotificationAppOutPort { notificationApp ->
            true.also {
                savableNotificationApp = notificationApp
            }
        }
        val service = createService(
            saveNotificationAppOutPort = captureAdapter
        )

        val isSaved = service.verify(command)

        assertTrue(isSaved)
        assertTrue(savableNotificationApp!!.isVerified)
    }

    private fun createService(
        findNotificationAppOutPort: FindNotificationAppOutPort = FindNotificationAppOutPort { notificationApp },
        saveNotificationAppOutPort: SaveNotificationAppOutPort = SaveNotificationAppOutPort { true }
    ) = VerifyNotificationAppCommandService(
        findNotificationAppOutPort = findNotificationAppOutPort,
        saveNotificationAppOutPort = saveNotificationAppOutPort,
    )
}

class VerifyNotificationAppCommandServiceTransactionalAnnotationTest {
    @Test
    fun `메서드 애너테이션 사용 여부 확인`() {
        assertTrue(MethodAnnotationTestHelper(VerifyNotificationAppCommandService::verify).hasWritableTransactionalAnnotation())
    }
}