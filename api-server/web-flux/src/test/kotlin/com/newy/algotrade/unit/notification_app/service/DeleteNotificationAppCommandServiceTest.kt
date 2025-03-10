package com.newy.algotrade.unit.notification_app.service

import com.newy.algotrade.common.exception.ForbiddenException
import com.newy.algotrade.common.exception.NotFoundRowException
import com.newy.algotrade.notification_app.domain.DeletableNotificationApp
import com.newy.algotrade.notification_app.port.`in`.model.DeleteNotificationAppCommand
import com.newy.algotrade.notification_app.port.out.DeleteNotificationAppOutPort
import com.newy.algotrade.notification_app.port.out.FindDeletableNotificationAppOutPort
import com.newy.algotrade.notification_app.service.DeleteNotificationAppCommandService
import helpers.spring.MethodAnnotationTestHelper
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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
        val service = createService(
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
        val service = createService(
            findDeletableNotificationAppOutPort = foundOtherUserNotificationAppAdapter,
        )

        val error = assertThrows<ForbiddenException> {
            service.deleteNotificationApp(command)
        }

        assertEquals("다른 User 의 NotificationApp 을 삭제할 수 없습니다.", error.message)
    }

    @Test
    fun `문제가 없는 요청이면 DeleteNotificationAppOutPort 에 notificationAppId 가 전달된다`() = runTest {
        var deletableNotificationId: Long? = null
        val service = createService(
            deleteNotificationAppOutPort = { deletableNotificationId = it },
        )

        service.deleteNotificationApp(command)

        assertEquals(command.notificationAppId, deletableNotificationId)
    }

    private fun createService(
        findDeletableNotificationAppOutPort: FindDeletableNotificationAppOutPort = FindDeletableNotificationAppOutPort {
            DeletableNotificationApp(userId = command.userId, id = command.notificationAppId)
        },
        deleteNotificationAppOutPort: DeleteNotificationAppOutPort = DeleteNotificationAppOutPort {},
    ) = DeleteNotificationAppCommandService(
        findDeletableNotificationAppOutPort,
        deleteNotificationAppOutPort,
    )
}

class DeleteNotificationAppCommandServiceAnnotationTest {
    @Test
    fun `메서드 애너테이션 사용 여부 확인`() {
        assertTrue(MethodAnnotationTestHelper(DeleteNotificationAppCommandService::deleteNotificationApp).hasWritableTransactionalAnnotation())
    }
}