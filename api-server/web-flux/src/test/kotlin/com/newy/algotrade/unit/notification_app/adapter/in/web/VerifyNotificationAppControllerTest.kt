package com.newy.algotrade.unit.notification_app.adapter.`in`.web

import com.newy.algotrade.notification_app.adapter.`in`.web.VerifyNotificationAppController
import com.newy.algotrade.notification_app.adapter.`in`.web.model.VerifyNotificationAppRequest
import com.newy.algotrade.notification_app.adapter.`in`.web.model.VerifyNotificationAppResponse
import com.newy.algotrade.notification_app.port.`in`.model.VerifyNotificationAppCommand
import com.newy.algotrade.spring.auth.model.LoginUser
import helpers.spring.AdminOnlyAnnotationTestHelper
import kotlinx.coroutines.test.runTest
import org.springframework.http.ResponseEntity
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

class VerifyNotificationAppControllerAnnotationTest :
    AdminOnlyAnnotationTestHelper(clazz = VerifyNotificationAppController::class) {
    @Test
    fun `@AdminOnly @LoginUser 애너테이션 사용 여부 테스트`() {
        assertTrue(hasAdminOnly(methodName = "verifyNotificationApp"))
        assertTrue(hasLoginUserInfo(methodName = "verifyNotificationApp", parameterName = "loginUser"))
    }
}