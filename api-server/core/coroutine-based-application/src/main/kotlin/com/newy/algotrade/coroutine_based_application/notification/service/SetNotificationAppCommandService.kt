package com.newy.algotrade.coroutine_based_application.notification.service

import com.newy.algotrade.coroutine_based_application.notification.port.`in`.SetNotificationAppUseCase
import com.newy.algotrade.coroutine_based_application.notification.port.`in`.model.SetNotificationAppCommand
import com.newy.algotrade.coroutine_based_application.notification.port.out.NotificationAppPort
import com.newy.algotrade.domain.common.exception.DuplicateDataException

open class SetNotificationAppCommandService(
    private val notificationAppPort: NotificationAppPort,
) : SetNotificationAppUseCase {
    override suspend fun setNotificationApp(command: SetNotificationAppCommand): Boolean {
        if (notificationAppPort.hasNotificationApp(command.userId)) {
            throw DuplicateDataException("이미 알림 앱을 등록했습니다.")
        }

        return notificationAppPort.setNotificationApp(command.toDomainEntity())
    }
}