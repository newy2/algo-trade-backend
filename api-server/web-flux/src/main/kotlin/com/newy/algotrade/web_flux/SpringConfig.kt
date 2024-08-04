package com.newy.algotrade.web_flux

import com.newy.algotrade.coroutine_based_application.notification.port.`in`.model.SendNotificationCommand
import com.newy.algotrade.coroutine_based_application.notification.service.SendNotificationCommandService
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.OnCreatedStrategySignalPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SpringConfig {
    @Bean
    open fun OnCreatedStrategySignalPort(
        sendNotificationService: SendNotificationCommandService,
    ): OnCreatedStrategySignalPort {
        // TODO
        return OnCreatedStrategySignalPort { userStrategyId, signal ->
            print("@@@userStrategyId: $userStrategyId, signal: $signal")
            sendNotificationService.requestSendNotification(
                SendNotificationCommand(
                    notificationAppId = 1,
                    requestMessage = "@@@userStrategyId: $userStrategyId, signal: $signal",
                )
            )
        }
    }
}

