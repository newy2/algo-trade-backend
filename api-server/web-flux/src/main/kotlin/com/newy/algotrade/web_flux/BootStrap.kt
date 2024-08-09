package com.newy.algotrade.web_flux

import com.newy.algotrade.coroutine_based_application.common.coroutine.EventBus
import com.newy.algotrade.coroutine_based_application.common.event.CreateStrategySignalEvent
import com.newy.algotrade.coroutine_based_application.common.event.CreateUserStrategyEvent
import com.newy.algotrade.coroutine_based_application.common.event.ReceivePollingPriceEvent
import com.newy.algotrade.coroutine_based_application.common.event.SendNotificationEvent
import com.newy.algotrade.coroutine_based_application.notification.port.`in`.model.SendNotificationCommand
import com.newy.algotrade.coroutine_based_application.notification.service.SendNotificationCommandService
import com.newy.algotrade.coroutine_based_application.product.adapter.`in`.internal_system.InitController
import com.newy.algotrade.coroutine_based_application.product.port.`in`.AddCandlesUseCase
import com.newy.algotrade.coroutine_based_application.product.port.out.PollingProductPricePort
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.RunStrategyUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.RunnableStrategyUseCase
import com.newy.algotrade.coroutine_based_application.strategy.port.`in`.StrategyQuery
import com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`.GetAllUserStrategyProductQuery
import com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`.UserStrategyProductQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("!test")
@Component
class Bootstrap(
    private val strategyQuery: StrategyQuery,
    private val runnableStrategyUseCase: RunnableStrategyUseCase,
    private val getAllUserStrategyProductQuery: GetAllUserStrategyProductQuery,
    private val pollingProductPricePort: PollingProductPricePort,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        runBlocking {
            strategyQuery.checkRegisteredStrategyClasses()
        }
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
    @Qualifier("sendNotificationEventBus") val sendNotificationEventBus: EventBus<SendNotificationEvent>,
    @Qualifier("receivePollingPriceEventBus") val receivePollingPriceEventBus: EventBus<ReceivePollingPriceEvent>,
    @Qualifier("createStrategySignalEventBus") val createStrategySignalEventBus: EventBus<CreateStrategySignalEvent>,
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
            sendNotificationEventBus.addListener(coroutineContext) {
                sendNotificationService.sendNotification(it)
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            receivePollingPriceEventBus.addListener(coroutineContext) { (productPriceKey, productPriceList) ->
                candlesUseCase.addCandles(productPriceKey, productPriceList)
                runStrategyUseCase.runStrategy(productPriceKey)
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            createStrategySignalEventBus.addListener(coroutineContext) { (userStrategyId, strategySignal) ->
                // TODO order 패키지로 이동
                print("@@@userStrategyId: $userStrategyId, signal: $strategySignal")
                sendNotificationService.requestSendNotification(
                    SendNotificationCommand(
                        notificationAppId = 1,
                        requestMessage = "@@@userStrategyId: $userStrategyId, signal: $strategySignal",
                    )
                )
            }
        }
    }
}