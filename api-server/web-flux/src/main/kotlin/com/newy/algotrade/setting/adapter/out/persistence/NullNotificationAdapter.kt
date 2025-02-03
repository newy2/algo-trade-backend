package com.newy.algotrade.setting.adapter.out.persistence

import com.newy.algotrade.setting.domain.NotificationApp
import com.newy.algotrade.setting.port.out.GetNotificationAppOutPort
import com.newy.algotrade.spring.annotation.PersistenceAdapter

@PersistenceAdapter
class NullNotificationAdapter : GetNotificationAppOutPort {
    override fun getNotificationApp(userId: Long): NotificationApp? {
        TODO("Not yet implemented")
    }
}