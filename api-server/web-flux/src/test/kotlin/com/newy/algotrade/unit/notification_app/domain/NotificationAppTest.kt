package com.newy.algotrade.unit.notification_app.domain

import com.newy.algotrade.notification_app.domain.CodeGenerator
import com.newy.algotrade.notification_app.domain.NotificationApp
import com.newy.algotrade.notification_app.domain.Webhook
import helpers.diffSeconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
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