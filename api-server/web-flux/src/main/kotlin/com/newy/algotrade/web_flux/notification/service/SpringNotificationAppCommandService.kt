package com.newy.algotrade.web_flux.notification.service

import com.newy.algotrade.coroutine_based_application.notification.port.out.NotificationAppPort
import com.newy.algotrade.coroutine_based_application.notification.service.NotificationAppCommandService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
open class SpringNotificationAppCommandService(
    notificationAppPort: NotificationAppPort,
) : NotificationAppCommandService(notificationAppPort)