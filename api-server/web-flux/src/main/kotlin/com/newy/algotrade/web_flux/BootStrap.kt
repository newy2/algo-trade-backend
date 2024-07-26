package com.newy.algotrade.web_flux

import com.newy.algotrade.coroutine_based_application.common.coroutine.EventBus
import com.newy.algotrade.coroutine_based_application.common.event.CreateUserStrategyEvent
import com.newy.algotrade.coroutine_based_application.common.event.SendNotificationEvent
import com.newy.algotrade.coroutine_based_application.notification.service.SendNotificationService
import com.newy.algotrade.coroutine_based_application.product.adapter.`in`.system.InitController
import com.newy.algotrade.coroutine_based_application.product.adapter.`in`.web.SetRunnableStrategyController
import com.newy.algotrade.coroutine_based_application.product.port.`in`.GetAllUserStrategyQuery
import com.newy.algotrade.coroutine_based_application.product.port.`in`.UserStrategyQuery
import com.newy.algotrade.coroutine_based_application.product.port.out.PollingProductPricePort
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
    private val setRunnableStrategyController: SetRunnableStrategyController,
    private val getAllUserStrategyQuery: GetAllUserStrategyQuery,
    private val pollingProductPricePort: PollingProductPricePort,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            pollingProductPricePort.start()
        }
        CoroutineScope(Dispatchers.IO).launch {
            InitController(setRunnableStrategyController, getAllUserStrategyQuery).init()
        }
    }
}

@Component
class RegisterEventHandler(
    private val setRunnableStrategyController: SetRunnableStrategyController,
    private val userStrategyQuery: UserStrategyQuery,
    private val sendNotificationService: SendNotificationService,
    @Qualifier("createUserStrategyEventBus") val createUserStrategyEventBus: EventBus<CreateUserStrategyEvent>,
    @Qualifier("createSendNotificationEventBus") val createSendNotificationEventBus: EventBus<SendNotificationEvent>,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            createUserStrategyEventBus.addListener(coroutineContext) {
                userStrategyQuery.getUserStrategy(it.id)?.let { userStrategyKey ->
                    setRunnableStrategyController.setUserStrategy(userStrategyKey)
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