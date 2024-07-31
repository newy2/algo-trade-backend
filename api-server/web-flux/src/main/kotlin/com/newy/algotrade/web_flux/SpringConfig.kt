package com.newy.algotrade.web_flux

import com.newy.algotrade.coroutine_based_application.notification.port.`in`.model.SendNotificationCommand
import com.newy.algotrade.coroutine_based_application.notification.service.SendNotificationService
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.OnCreatedStrategySignalPort
import com.newy.algotrade.domain.chart.strategy.StrategySignal
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SpringConfig {
    @Bean
    open fun OnCreatedStrategySignalPort(
        sendNotificationService: SendNotificationService,
    ): OnCreatedStrategySignalPort {
        // TODO
        return object : OnCreatedStrategySignalPort {
            override suspend fun onCreatedSignal(userStrategyId: String, signal: StrategySignal) {
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
}

