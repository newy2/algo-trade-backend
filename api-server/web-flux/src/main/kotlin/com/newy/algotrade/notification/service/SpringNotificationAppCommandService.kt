package com.newy.algotrade.notification.service

import com.newy.algotrade.notification.port.out.NotificationAppPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
open class SpringNotificationAppCommandService(
    notificationAppPort: NotificationAppPort,
) : NotificationAppCommandService(notificationAppPort)