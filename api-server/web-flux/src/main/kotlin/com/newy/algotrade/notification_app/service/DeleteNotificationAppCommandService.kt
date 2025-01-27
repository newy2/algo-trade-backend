package com.newy.algotrade.notification_app.service

import com.newy.algotrade.common.exception.NotFoundRowException
import com.newy.algotrade.notification_app.domain.DeletableNotificationApp
import com.newy.algotrade.notification_app.port.`in`.DeleteNotificationAppInPort
import com.newy.algotrade.notification_app.port.`in`.model.DeleteNotificationAppCommand
import com.newy.algotrade.notification_app.port.out.DeleteNotificationAppOutPort
import com.newy.algotrade.notification_app.port.out.FindDeletableNotificationAppOutPort
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteNotificationAppCommandService(
    @Autowired private val findDeletableNotificationAppOutPort: FindDeletableNotificationAppOutPort,
    @Autowired private val deleteNotificationAppOutPort: DeleteNotificationAppOutPort,
) : DeleteNotificationAppInPort {
    @Transactional
    override suspend fun deleteNotificationApp(command: DeleteNotificationAppCommand) {
        findNotificationApp(command).checkOwnership(command.userId)
        deleteNotificationAppOutPort.deleteById(command.notificationAppId)
    }

    private suspend fun findNotificationApp(command: DeleteNotificationAppCommand): DeletableNotificationApp =
        findDeletableNotificationAppOutPort.findById(command.notificationAppId)
            ?: throw NotFoundRowException("NotificationApp 을 찾을 수 없습니다. ($command)")

}