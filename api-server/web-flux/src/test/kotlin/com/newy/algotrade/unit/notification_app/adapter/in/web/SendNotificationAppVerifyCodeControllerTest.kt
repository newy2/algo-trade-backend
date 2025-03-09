package com.newy.algotrade.unit.notification_app.adapter.`in`.web

import com.newy.algotrade.notification_app.adapter.`in`.web.SendNotificationAppVerifyCodeController
import com.newy.algotrade.notification_app.adapter.`in`.web.model.SendNotificationAppVerifyCodeRequest
import com.newy.algotrade.notification_app.adapter.`in`.web.model.SendNotificationAppVerifyCodeResponse
import com.newy.algotrade.notification_app.port.`in`.model.SendNotificationAppVerifyCodeCommand
import com.newy.algotrade.spring.auth.model.LoginUser
import helpers.spring.ClassAnnotationTestHelper
import helpers.spring.MethodAnnotationTestHelper
import kotlinx.coroutines.test.runTest
import org.springframework.http.ResponseEntity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SendNotificationAppVerifyCodeControllerTest {
    private val webRequestModel = object {
        val loginUser = LoginUser(1.toLong())
        val request = SendNotificationAppVerifyCodeRequest(
            type = "SLACK",
            webhookUrl = "https://hooks.slack.com/services/1111",
        )
    }

    @Test
    fun `Controller 는 WebRequest 모델을 InPort 모델로 변환해야 한다`() = runTest {
        var inPortModel: SendNotificationAppVerifyCodeCommand? = null
        val controller = SendNotificationAppVerifyCodeController { command ->
            "".also {
                inPortModel = command
            }
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
    fun `요청이 성공하면 200 상태를 응답한다`() = runTest {
        val controller = SendNotificationAppVerifyCodeController { "A1B2C" }
        val response = controller.sendVerifyCode(
            loginUser = webRequestModel.loginUser,
            request = webRequestModel.request
        )

        assertEquals(
            ResponseEntity.ok(
                SendNotificationAppVerifyCodeResponse(
                    webhookUrl = "https://hooks.slack.com/services/1111",
                    verifyCode = "A1B2C"
                )
            ),
            response
        )
    }
}

class SendNotificationAppVerifyCodeControllerAnnotationTest {
    @Test
    fun `클래스 애너테이션 사용 여부 확인`() {
        val helper = ClassAnnotationTestHelper(SendNotificationAppVerifyCodeController::class)
        assertTrue(helper.hasRestControllerAnnotation())
    }

    @Test
    fun `메서드 애너테이션 사용 여부 확인`() {
        MethodAnnotationTestHelper(SendNotificationAppVerifyCodeController::sendVerifyCode).let {
            assertTrue(it.hasPostMappingAnnotation("/setting/notification/verify-code/publish"))
            assertTrue(it.hasAdminOnlyAnnotation())
            assertTrue(it.hasLoginUserInfoAnnotation(parameterName = "loginUser"))
        }
    }
}