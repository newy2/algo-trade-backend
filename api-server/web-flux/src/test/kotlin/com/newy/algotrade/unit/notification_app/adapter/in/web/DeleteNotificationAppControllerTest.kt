package com.newy.algotrade.unit.notification_app.adapter.`in`.web

import com.newy.algotrade.notification_app.adapter.`in`.web.DeleteNotificationAppController
import com.newy.algotrade.notification_app.port.`in`.model.DeleteNotificationAppCommand
import com.newy.algotrade.spring.auth.model.LoginUser
import helpers.spring.ClassAnnotationTestHelper
import helpers.spring.MethodAnnotationTestHelper
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
        var inPortModel: DeleteNotificationAppCommand? = null
        val controller = DeleteNotificationAppController { command ->
            inPortModel = command
        }
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
    fun `요청이 성공하면 204 상태를 응답한다`() = runTest {
        val controller = DeleteNotificationAppController {}
        val response = controller.deleteNotificationApp(
            loginUser = webRequestModel.loginUser,
            notificationAppId = webRequestModel.notificationAppId,
        )

        assertEquals(ResponseEntity.noContent().build<Unit>(), response)
    }
}

class DeleteNotificationAppControllerAnnotationTest {
    @Test
    fun `클래스 애너테이션 사용 여부 확인`() {
        val helper = ClassAnnotationTestHelper(DeleteNotificationAppController::class)
        assertTrue(helper.hasRestControllerAnnotation())
    }

    @Test
    fun `메서드 애너테이션 사용 여부 확인`() {
        MethodAnnotationTestHelper(DeleteNotificationAppController::deleteNotificationApp).let {
            assertTrue(it.hasDeleteMappingAnnotation("/setting/notification/{notificationAppId}"))
            assertTrue(it.hasAdminOnlyAnnotation())
            assertTrue(it.hasLoginUserInfoAnnotation(parameterName = "loginUser"))
        }
    }
}