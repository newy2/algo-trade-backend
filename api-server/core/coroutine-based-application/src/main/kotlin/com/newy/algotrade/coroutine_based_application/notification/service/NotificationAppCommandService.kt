package com.newy.algotrade.coroutine_based_application.notification.service

import com.newy.algotrade.coroutine_based_application.notification.port.`in`.NotificationAppUseCase
import com.newy.algotrade.coroutine_based_application.notification.port.`in`.model.SetNotificationAppCommand
import com.newy.algotrade.coroutine_based_application.notification.port.out.HasNotificationAppPort
import com.newy.algotrade.coroutine_based_application.notification.port.out.NotificationAppPort
import com.newy.algotrade.coroutine_based_application.notification.port.out.SetNotificationAppPort
import com.newy.algotrade.domain.common.exception.DuplicateDataException

open class NotificationAppCommandService(
    private val hasNotificationAppPort: HasNotificationAppPort,
    private val setNotificationAppPort: SetNotificationAppPort,
) : NotificationAppUseCase {
    constructor(notificationAppPort: NotificationAppPort) : this(
        hasNotificationAppPort = notificationAppPort,
        setNotificationAppPort = notificationAppPort,
    )

    override suspend fun setNotificationApp(command: SetNotificationAppCommand): Boolean {
        if (hasNotificationAppPort.hasNotificationApp(command.userId)) {
            throw DuplicateDataException("이미 알림 앱을 등록했습니다.")
        }

        return setNotificationAppPort.setNotificationApp(command.toDomainEntity())
    }
}