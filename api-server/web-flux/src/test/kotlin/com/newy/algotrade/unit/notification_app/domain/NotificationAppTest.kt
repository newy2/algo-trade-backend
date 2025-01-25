package com.newy.algotrade.unit.notification_app.domain

import com.newy.algotrade.common.exception.VerificationCodeException
import com.newy.algotrade.notification_app.domain.CodeGenerator
import com.newy.algotrade.notification_app.domain.NotificationApp
import com.newy.algotrade.notification_app.domain.Webhook
import helpers.diffSeconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import kotlin.properties.Delegates
import kotlin.test.*

@DisplayName("인증코드 생성 테스트")
class NotificationAppImplementTest : CodeGenerator() {
    var isCalled by Delegates.notNull<Boolean>()
    var excludeCode by Delegates.notNull<String>()

    override fun generate(excludeCode: String): String {
        this.isCalled = true
        this.excludeCode = excludeCode

        return super.generate(excludeCode)
    }

    @BeforeTest
    fun setUp() {
        isCalled = false
        excludeCode = ""
    }

    @Test
    fun `인증코드는 CodeGenerator 를 사용해서 생성한다`() {
        val notificationApp = NotificationApp(
            userId = 1,
            webhook = Webhook(
                type = "SLACK",
                url = "https://hooks.slack.com/services/1111",
            ),
            codeGenerator = this,
        )

        val newNotificationApp = notificationApp.generateVerifyCode()

        assertTrue(this.isCalled)
        assertEquals(5, newNotificationApp.verifyCode.length)
    }

    @Test
    fun `인증코드를 재생성할 때 기존에 사용한 verifyCode 를 제외하고 생성한다`() {
        val oldVerifyCode = "A1B2C"
        val notificationApp = NotificationApp(
            userId = 1,
            webhook = Webhook(
                type = "SLACK",
                url = "https://hooks.slack.com/services/1111",
            ),
            codeGenerator = this,
            verifyCode = oldVerifyCode,
        )

        val newNotificationApp = notificationApp.generateVerifyCode()
        assertEquals(oldVerifyCode, this.excludeCode)
        assertNotEquals(oldVerifyCode, newNotificationApp.verifyCode)
    }

    @Test
    fun `인증코드의 기본 유효기간은 3분이다`() {
        val notificationApp = NotificationApp(
            userId = 1,
            webhook = Webhook(
                type = "SLACK",
                url = "https://hooks.slack.com/services/1111",
            ),
        )

        val now = LocalDateTime.now()
        val newNotificationApp = notificationApp.generateVerifyCode()

        assertEquals(3 * 60, diffSeconds(now, newNotificationApp.expiredAt))
    }

    @Test
    fun `인증코드를 재발급하면 유효기간이 업데이트 된다`() = runBlocking {
        val notificationApp = NotificationApp(
            userId = 1,
            webhook = Webhook(
                type = "SLACK",
                url = "https://hooks.slack.com/services/1111",
            ),
        )

        val oldNotificationApp = notificationApp.generateVerifyCode()
        delay(1000)
        val newNotificationApp = oldNotificationApp.generateVerifyCode()

        assertEquals(1, diffSeconds(oldNotificationApp.expiredAt, newNotificationApp.expiredAt))
        assertNotEquals(newNotificationApp.verifyCode, oldNotificationApp.verifyCode)
    }
}

@DisplayName("인증코드 검증 테스트")
class VerifyNotificationAppTest {
    private val notificationApp = NotificationApp(
        userId = 1,
        webhook = Webhook(
            type = "SLACK",
            url = "https://hooks.slack.com/services/1111",
        ),
        verifyCode = "ABCDE",
        expiredAt = LocalDateTime.now().plusMinutes(3),
        isVerified = false,
    )

    @Test
    fun `expiredAt 이후에 검증을 요청하면 에러를 발생한다`() {
        /**
         * Functional interface 에서 default value 를 지원하지 않아서, 도메인 모델의 테스트 코드를 유지한다.
         * 관련 이슈: https://discuss.kotlinlang.org/t/functional-interfaces-parameter-default-value/25795
         * */
        
        val now = LocalDateTime.now()

        val notificationApp = notificationApp.copy(
            expiredAt = now
        )

        val error = assertThrows<VerificationCodeException> {
            notificationApp.verify(verifyCode = "ABCDE", now = now.plusSeconds(1))
        }

        assertDoesNotThrow { notificationApp.verify(verifyCode = "ABCDE", now = now) }
        assertDoesNotThrow { notificationApp.verify(verifyCode = "ABCDE", now = now.minusSeconds(1)) }
        assertEquals("검증 가능 시간을 초과했습니다.", error.message)
    }

    @Test
    fun `인증이 성공하면 isVerified 가 true 인 object 가 리턴된다`() {
        val newNotificationApp = notificationApp.verify("ABCDE")

        assertFalse(notificationApp.isVerified)
        assertTrue(newNotificationApp.isVerified)
    }
}
