package com.newy.algotrade.web_flux

import com.newy.algotrade.coroutine_based_application.common.coroutine.EventBus
import com.newy.algotrade.coroutine_based_application.common.event.CreateUserStrategyEvent
import com.newy.algotrade.coroutine_based_application.common.event.SendNotificationEvent
import com.newy.algotrade.coroutine_based_application.notification.service.SendNotificationCommandService
import com.newy.algotrade.coroutine_based_application.product.adapter.`in`.system.InitController
import com.newy.algotrade.coroutine_based_application.product.port.out.PollingProductPricePort
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.RunnableStrategyUseCase
import com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`.GetAllUserStrategyQuery
import com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`.UserStrategyQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("!test")
@Component
class Bootstrap(
    private val runnableStrategyUseCase: RunnableStrategyUseCase,
    private val getAllUserStrategyQuery: GetAllUserStrategyQuery,
    private val pollingProductPricePort: PollingProductPricePort,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            pollingProductPricePort.start()
        }
        CoroutineScope(Dispatchers.IO).launch {
            InitController(runnableStrategyUseCase, getAllUserStrategyQuery).init()
        }
    }
}

@Component
class RegisterEventHandler(
    private val runnableStrategyUseCase: RunnableStrategyUseCase,
    private val userStrategyQuery: UserStrategyQuery,
    private val sendNotificationService: SendNotificationCommandService,
    @Qualifier("createUserStrategyEventBus") val createUserStrategyEventBus: EventBus<CreateUserStrategyEvent>,
    @Qualifier("createSendNotificationEventBus") val createSendNotificationEventBus: EventBus<SendNotificationEvent>,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            createUserStrategyEventBus.addListener(coroutineContext) {
                userStrategyQuery.getUserStrategies(it.id).forEach { eachUserStrategyKey ->
                    runnableStrategyUseCase.setRunnableStrategy(eachUserStrategyKey)
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            createSendNotificationEventBus.addListener(coroutineContext) {
                sendNotificationService.sendNotification(it)
            }
        }
    }
}