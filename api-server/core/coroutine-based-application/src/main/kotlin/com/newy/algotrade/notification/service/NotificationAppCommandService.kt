package com.newy.algotrade.notification.service

import com.newy.algotrade.domain.common.exception.DuplicateDataException
import com.newy.algotrade.notification.port.`in`.NotificationAppUseCase
import com.newy.algotrade.notification.port.`in`.model.SetNotificationAppCommand
import com.newy.algotrade.notification.port.out.ExistsHasNotificationAppPort
import com.newy.algotrade.notification.port.out.NotificationAppPort
import com.newy.algotrade.notification.port.out.SaveNotificationAppPort

open class NotificationAppCommandService(
    private val existsHasNotificationAppPort: ExistsHasNotificationAppPort,
    private val saveNotificationAppPort: SaveNotificationAppPort,
) : NotificationAppUseCase {
    constructor(notificationAppPort: NotificationAppPort) : this(
        existsHasNotificationAppPort = notificationAppPort,
        saveNotificationAppPort = notificationAppPort,
    )

    override suspend fun setNotificationApp(command: SetNotificationAppCommand): Boolean {
        if (existsHasNotificationAppPort.existsNotificationApp(command.userId)) {
            throw DuplicateDataException("이미 알림 앱을 등록했습니다.")
        }

        return saveNotificationAppPort.saveNotificationApp(command.toDomainEntity())
    }
}