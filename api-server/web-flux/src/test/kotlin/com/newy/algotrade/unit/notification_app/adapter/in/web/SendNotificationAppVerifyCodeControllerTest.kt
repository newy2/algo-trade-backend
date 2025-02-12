package com.newy.algotrade.unit.notification_app.adapter.`in`.web

import com.newy.algotrade.notification_app.adapter.`in`.web.SendNotificationAppVerifyCodeController
import com.newy.algotrade.notification_app.adapter.`in`.web.model.SendNotificationAppVerifyCodeRequest
import com.newy.algotrade.notification_app.adapter.`in`.web.model.SendNotificationAppVerifyCodeResponse
import com.newy.algotrade.notification_app.port.`in`.model.SendNotificationAppVerifyCodeCommand
import com.newy.algotrade.spring.auth.model.LoginUser
import helpers.spring.AdminOnlyAnnotationTestHelper
import kotlinx.coroutines.test.runTest
import org.springframework.http.ResponseEntity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SendNotificationAppVerifyCodeControllerTest {
    @Test
    fun `Controller 는 WebRequest 모델을 InPort 모델로 변환해야 한다`() = runTest {
        lateinit var inPortModel: SendNotificationAppVerifyCodeCommand
        val controller = SendNotificationAppVerifyCodeController(
            sendNotificationAppVerifyCodeInPort = { command ->
                "".also {
                    inPortModel = command
                }
            }
        )
        val webRequestModel = object {
            val loginUser = LoginUser(1.toLong())
            val request = SendNotificationAppVerifyCodeRequest(
                type = "SLACK",
                webhookUrl = "https://hooks.slack.com/services/1111",
            )
        }

        controller.sendVerifyCode(
            loginUser = webRequestModel.loginUser,
            request = webRequestModel.request
        )

        assertEquals(
            SendNotificationAppVerifyCodeCommand(
                userId = 1,
                webhookType = "SLACK",
                webhookUrl = "https://hooks.slack.com/services/1111",
            ),
            inPortModel
        )
    }

    @Test
    fun `요청이 성공하면 verifyCode 를 응답한다`() = runTest {
        val verifyCode = "A1B2C"
        val controller = SendNotificationAppVerifyCodeController(
            sendNotificationAppVerifyCodeInPort = { verifyCode }
        )

        val response = controller.sendVerifyCode(
            loginUser = LoginUser(1),
            request = SendNotificationAppVerifyCodeRequest(
                type = "SLACK",
                webhookUrl = "https://hooks.slack.com/services/1111",
            )
        )

        assertEquals(
            ResponseEntity.ok(
                SendNotificationAppVerifyCodeResponse(
                    webhookUrl = "https://hooks.slack.com/services/1111",
                    verifyCode = verifyCode
                )
            ),
            response
        )
    }
}

class SendNotificationAppVerifyCodeControllerAnnotationTest :
    AdminOnlyAnnotationTestHelper(clazz = SendNotificationAppVerifyCodeController::class) {
    @Test
    fun `@AdminOnly @LoginUser 애너테이션 사용 여부 테스트`() {
        assertTrue(hasAdminOnly(methodName = "sendVerifyCode"))
        assertTrue(hasLoginUserInfo(methodName = "sendVerifyCode", parameterName = "loginUser"))
    }
}