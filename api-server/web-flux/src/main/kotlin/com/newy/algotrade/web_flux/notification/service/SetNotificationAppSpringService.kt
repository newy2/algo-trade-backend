package com.newy.algotrade.web_flux.notification.service

import com.newy.algotrade.coroutine_based_application.notification.port.out.NotificationAppPort
import com.newy.algotrade.coroutine_based_application.notification.service.SetNotificationAppService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
open class SetNotificationAppSpringService(
    notificationAppPort: NotificationAppPort,
) : SetNotificationAppService(notificationAppPort)