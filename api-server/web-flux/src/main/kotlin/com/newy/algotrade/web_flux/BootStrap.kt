package com.newy.algotrade.web_flux

import com.newy.algotrade.coroutine_based_application.common.coroutine.EventBus
import com.newy.algotrade.coroutine_based_application.common.event.CreateUserStrategyEvent
import com.newy.algotrade.coroutine_based_application.common.event.ReceivePollingPriceEvent
import com.newy.algotrade.coroutine_based_application.common.event.SendNotificationEvent
import com.newy.algotrade.coroutine_based_application.notification.service.SendNotificationCommandService
import com.newy.algotrade.coroutine_based_application.product.adapter.`in`.internal_system.InitController
import com.newy.algotrade.coroutine_based_application.product.port.`in`.AddCandlesUseCase
import com.newy.algotrade.coroutine_based_application.product.port.out.PollingProductPricePort
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.RunStrategyUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.RunnableStrategyUseCase
import com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`.GetAllUserStrategyProductQuery
import com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`.UserStrategyProductQuery
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
    private val getAllUserStrategyProductQuery: GetAllUserStrategyProductQuery,
    private val pollingProductPricePort: PollingProductPricePort,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            pollingProductPricePort.start()
        }
        CoroutineScope(Dispatchers.IO).launch {
            InitController(runnableStrategyUseCase, getAllUserStrategyProductQuery).init()
        }
    }
}

@Component
class RegisterEventHandler(
    private val runnableStrategyUseCase: RunnableStrategyUseCase,
    private val userStrategyProductQuery: UserStrategyProductQuery,
    private val sendNotificationService: SendNotificationCommandService,
    private val candlesUseCase: AddCandlesUseCase,
    private val runStrategyUseCase: RunStrategyUseCase,
    @Qualifier("createUserStrategyEventBus") val createUserStrategyEventBus: EventBus<CreateUserStrategyEvent>,
    @Qualifier("createSendNotificationEventBus") val createSendNotificationEventBus: EventBus<SendNotificationEvent>,
    @Qualifier("createReceivePollingPriceEventBus") val createReceivePollingPriceEventBus: EventBus<ReceivePollingPriceEvent>,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            createUserStrategyEventBus.addListener(coroutineContext) {
                userStrategyProductQuery.getUserStrategyKeys(it.id).forEach { eachUserStrategyKey ->
                    runnableStrategyUseCase.setRunnableStrategy(eachUserStrategyKey)
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            createSendNotificationEventBus.addListener(coroutineContext) {
                sendNotificationService.sendNotification(it)
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            createReceivePollingPriceEventBus.addListener(coroutineContext) { (productPriceKey, productPriceList) ->
                candlesUseCase.addCandles(productPriceKey, productPriceList)
                runStrategyUseCase.runStrategy(productPriceKey)
            }
        }
    }
}