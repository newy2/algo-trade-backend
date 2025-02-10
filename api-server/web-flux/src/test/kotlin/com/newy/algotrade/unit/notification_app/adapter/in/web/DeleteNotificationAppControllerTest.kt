package com.newy.algotrade.unit.notification_app.adapter.`in`.web

import com.newy.algotrade.notification_app.adapter.`in`.web.DeleteNotificationAppController
import com.newy.algotrade.notification_app.port.`in`.model.DeleteNotificationAppCommand
import com.newy.algotrade.spring.auth.model.LoginUser
import helpers.spring.AdminOnlyAnnotationTestHelper
import kotlinx.coroutines.test.runTest
import org.springframework.http.ResponseEntity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeleteNotificationAppControllerTest {
    private val webRequestModel = object {
        val loginUser = LoginUser(id = 1)
        val notificationAppId: Long = 2
    }

    @Test
    fun `Controller 는 WebRequest 모델을 InPort 모델로 변환해야 한다`() = runTest {
        lateinit var inPortModel: DeleteNotificationAppCommand
        val controller = DeleteNotificationAppController(
            deleteNotificationAppInPort = { command ->
                inPortModel = command
            }
        )

        controller.deleteNotificationApp(
            loginUser = webRequestModel.loginUser,
            notificationAppId = webRequestModel.notificationAppId,
        )

        assertEquals(
            DeleteNotificationAppCommand(
                userId = 1,
                notificationAppId = 2
            ),
            inPortModel
        )
    }

    @Test
    fun `요청이 성공하면 No Content 를 응답한다`() = runTest {
        val controller = DeleteNotificationAppController(
            deleteNotificationAppInPort = { }
        )

        val response = controller.deleteNotificationApp(
            loginUser = webRequestModel.loginUser,
            notificationAppId = webRequestModel.notificationAppId,
        )

        assertEquals(ResponseEntity.noContent().build<Unit>(), response)
    }
}

class DeleteNotificationAppControllerAnnotationTest :
    AdminOnlyAnnotationTestHelper(clazz = DeleteNotificationAppController::class) {
    @Test
    fun `@AdminOnly @LoginUser 애너테이션 사용 여부 테스트`() {
        assertTrue(hasAdminOnly(methodName = "deleteNotificationApp"))
        assertTrue(hasLoginUserInfo(methodName = "deleteNotificationApp", parameterName = "loginUser"))
    }
}