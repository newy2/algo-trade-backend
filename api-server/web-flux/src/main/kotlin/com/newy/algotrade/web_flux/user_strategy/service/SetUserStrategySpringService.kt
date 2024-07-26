package com.newy.algotrade.web_flux.user_strategy.service

import com.newy.algotrade.coroutine_based_application.common.coroutine.EventBus
import com.newy.algotrade.coroutine_based_application.common.event.CreateUserStrategyEvent
import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.*
import com.newy.algotrade.coroutine_based_application.user_strategy.service.SetUserStrategyService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
open class SetUserStrategySpringService(
    marketPort: GetMarketPort,
    strategyPort: HasStrategyPort,
    productPort: GetProductPort,
    userStrategyPort: UserStrategyPort,
    userStrategyProductPort: SetUserStrategyProductPort,
    @Qualifier("createUserStrategyEventBus") eventBus: EventBus<CreateUserStrategyEvent>,
) : SetUserStrategyService(marketPort, strategyPort, productPort, userStrategyPort, userStrategyProductPort, eventBus)