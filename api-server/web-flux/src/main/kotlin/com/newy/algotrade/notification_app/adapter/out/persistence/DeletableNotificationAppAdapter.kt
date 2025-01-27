package com.newy.algotrade.notification_app.adapter.out.persistence

import com.newy.algotrade.notification_app.domain.DeletableNotificationApp
import com.newy.algotrade.notification_app.port.out.DeleteNotificationAppOutPort
import com.newy.algotrade.notification_app.port.out.FindDeletableNotificationAppOutPort
import com.newy.algotrade.spring.annotation.PersistenceAdapter

@PersistenceAdapter
class DeletableNotificationAppAdapter : DeleteNotificationAppOutPort, FindDeletableNotificationAppOutPort {
    override suspend fun deleteById(notificationAppId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun findById(notificationAppId: Long): DeletableNotificationApp? {
        TODO("Not yet implemented")
    }
}