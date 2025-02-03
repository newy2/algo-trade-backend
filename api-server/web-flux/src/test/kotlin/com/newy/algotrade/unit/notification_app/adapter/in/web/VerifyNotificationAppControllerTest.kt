package com.newy.algotrade.unit.notification_app.adapter.`in`.web

import com.newy.algotrade.notification_app.adapter.`in`.web.VerifyNotificationAppController
import com.newy.algotrade.notification_app.adapter.`in`.web.model.VerifyNotificationAppRequest
import com.newy.algotrade.notification_app.adapter.`in`.web.model.VerifyNotificationAppResponse
import com.newy.algotrade.notification_app.port.`in`.model.VerifyNotificationAppCommand
import com.newy.algotrade.spring.auth.annotation.AdminOnly
import com.newy.algotrade.spring.auth.annotation.AdminUser
import com.newy.algotrade.spring.auth.model.LoginUser
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.springframework.http.ResponseEntity
import kotlin.reflect.full.functions
import kotlin.reflect.full.hasAnnotation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class VerifyNotificationAppControllerTest {
    private val webRequestModel = object {
        val loginUser = LoginUser(1.toLong())
        val request = VerifyNotificationAppRequest(
            verifyCode = "ABCDE",
        )
    }

    @Test
    fun `Controller 는 WebRequest 모델을 InPort 모델로 변환해야 한다`() = runTest {
        lateinit var inPortModel: VerifyNotificationAppCommand
        val controller = VerifyNotificationAppController(
            verifyNotificationAppInPort = { command ->
                true.also {
                    inPortModel = command
                }
            }
        )

        controller.verifyNotificationApp(
            loginUser = webRequestModel.loginUser,
            request = webRequestModel.request
        )

        assertEquals(
            VerifyNotificationAppCommand(
                userId = 1,
                verifyCode = "ABCDE",
            ),
            inPortModel
        )
    }

    @Test
    fun `요청이 성공하면 isSuccess 를 응답한다`() = runTest {
        val responseValue = true
        val controller = VerifyNotificationAppController(
            verifyNotificationAppInPort = { responseValue }
        )

        val response = controller.verifyNotificationApp(
            loginUser = webRequestModel.loginUser,
            request = webRequestModel.request
        )

        assertEquals(
            ResponseEntity.ok(
                VerifyNotificationAppResponse(
                    isSuccess = true
                )
            ),
            response
        )
    }
}

@DisplayName("Controller 에 인증 관련 애너테이션 사용 여부 확인하기")
class VerifyNotificationAppControllerAnnotationTest {
    private val methodName = "verifyNotificationApp"

    @Test
    fun `sendVerifyCode 메서드는 @AdminOnly 애너테이션을 선언해야 한다`() {
        val method = VerifyNotificationAppController::class.functions.find { it.name == methodName }!!

        assertTrue(method.hasAnnotation<AdminOnly>())
    }

    @Test
    fun `sendVerifyCode 메서드의 currentUser 파라미터는 @AdminUser 애너테이션을 선언해야 한다`() {
        val method = VerifyNotificationAppController::class.functions.find { it.name == methodName }!!
        val parameter = method.parameters.find { it.name == "currentUser" }!!

        assertTrue(parameter.hasAnnotation<AdminUser>())
    }
}