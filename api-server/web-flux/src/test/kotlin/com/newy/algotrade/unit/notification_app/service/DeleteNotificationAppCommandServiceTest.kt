package com.newy.algotrade.unit.notification_app.service

import com.newy.algotrade.common.exception.ForbiddenException
import com.newy.algotrade.common.exception.NotFoundRowException
import com.newy.algotrade.notification_app.domain.DeletableNotificationApp
import com.newy.algotrade.notification_app.port.`in`.model.DeleteNotificationAppCommand
import com.newy.algotrade.notification_app.port.out.DeleteNotificationAppOutPort
import com.newy.algotrade.notification_app.port.out.FindDeletableNotificationAppOutPort
import com.newy.algotrade.notification_app.service.DeleteNotificationAppCommandService
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.transaction.annotation.Transactional
import kotlin.reflect.full.functions
import kotlin.reflect.full.hasAnnotation
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeleteNotificationAppCommandServiceTest {
    private val command = DeleteNotificationAppCommand(
        userId = 1,
        notificationAppId = 2,
    )

    @Test
    fun `등록되지 않은 NotificationApp 을 삭제하는 경우 에러가 발생한다`() = runTest {
        val notFoundAdapter = FindDeletableNotificationAppOutPort { null }
        val service = newService(
            findDeletableNotificationAppOutPort = notFoundAdapter,
        )

        val error = assertThrows<NotFoundRowException> {
            service.deleteNotificationApp(command)
        }

        assertEquals(
            "NotificationApp 을 찾을 수 없습니다. (DeleteNotificationAppCommand(userId=1, notificationAppId=2))",
            error.message
        )
    }

    @Test
    fun `UserId 가 다른 NotificationApp 을 삭제하는 경우 에러가 발생한다`() = runTest {
        val foundOtherUserNotificationAppAdapter = FindDeletableNotificationAppOutPort {
            DeletableNotificationApp(
                userId = 10,
                id = 2,
            )
        }
        val service = newService(
            findDeletableNotificationAppOutPort = foundOtherUserNotificationAppAdapter,
        )

        val error = assertThrows<ForbiddenException> {
            service.deleteNotificationApp(command)
        }

        assertEquals("다른 User 의 NotificationApp 을 삭제할 수 없습니다.", error.message)
    }

    @Test
    fun `문제가 없는 요청이면 FindDeletableNotificationAppOutPort 과 DeleteNotificationAppOutPort 에 notificationAppId 가 전달된다`() =
        runTest {
            var findAdapterParameter: Long? = null
            var deleteAdapterParameter: Long? = null
            val service = newService(
                findDeletableNotificationAppOutPort = { parameter ->
                    defaultFindDeletableNotificationAppAdapter().findById(parameter).also {
                        findAdapterParameter = parameter
                    }
                },
                deleteNotificationAppOutPort = { parameter ->
                    deleteAdapterParameter = parameter
                },
            )

            service.deleteNotificationApp(command)

            assertEquals(command.notificationAppId, findAdapterParameter)
            assertEquals(command.notificationAppId, deleteAdapterParameter)
        }

    private fun newService(
        findDeletableNotificationAppOutPort: FindDeletableNotificationAppOutPort = defaultFindDeletableNotificationAppAdapter(),
        deleteNotificationAppOutPort: DeleteNotificationAppOutPort = defaultDeleteNotificationAppAdapter(),
    ) = DeleteNotificationAppCommandService(
        findDeletableNotificationAppOutPort,
        deleteNotificationAppOutPort,
    )

    private fun defaultFindDeletableNotificationAppAdapter() = FindDeletableNotificationAppOutPort {
        DeletableNotificationApp(
            userId = 1,
            id = 2,
        )
    }

    private fun defaultDeleteNotificationAppAdapter() = DeleteNotificationAppOutPort {}
}

@DisplayName("애너테이션 사용 여부 테스트")
class DeleteNotificationAppCommandServiceAnnotationTest {
    @Test
    fun `verify 메서드는 @Transactional 애너테이션을 선언해야 한다`() {
        val method = DeleteNotificationAppCommandService::class.functions.find { it.name == "deleteNotificationApp" }!!

        assertTrue(method.hasAnnotation<Transactional>())
    }
}