package com.newy.algotrade.unit.notification_app.adapter.`in`.web

import com.newy.algotrade.notification_app.adapter.`in`.web.DeleteNotificationAppController
import com.newy.algotrade.notification_app.port.`in`.model.DeleteNotificationAppCommand
import com.newy.algotrade.spring.auth.annotation.AdminOnly
import com.newy.algotrade.spring.auth.annotation.AdminUser
import com.newy.algotrade.spring.auth.model.LoginUser
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.springframework.http.ResponseEntity
import kotlin.reflect.KFunction
import kotlin.reflect.full.functions
import kotlin.reflect.full.hasAnnotation
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
            currentUser = webRequestModel.loginUser,
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
            currentUser = webRequestModel.loginUser,
            notificationAppId = webRequestModel.notificationAppId,
        )

        assertEquals(ResponseEntity.noContent().build<Unit>(), response)
    }
}

@DisplayName("Controller 에 인증 관련 애너테이션 사용 여부 확인하기")
class DeleteNotificationAppControllerAnnotationTest {
    private val methodName = "deleteNotificationApp"

    @Test
    fun `deleteNotificationApp 메서드는 @AdminOnly 애너테이션을 선언해야 한다`() {
        assertTrue(findMethod(methodName).hasAnnotation<AdminOnly>())
    }

    @Test
    fun `deleteNotificationApp 메서드의 currentUser 파라미터는 @AdminUser 애너테이션을 선언해야 한다`() {
        val parameter = findMethod(methodName).parameters.find { it.name == "currentUser" }!!
        assertTrue(parameter.hasAnnotation<AdminUser>())
    }

    private fun findMethod(methodName: String): KFunction<*> =
        DeleteNotificationAppController::class.functions.find { it.name == methodName }!!
}