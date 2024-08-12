package com.newy.algotrade.web_flux.user_strategy.service

import com.newy.algotrade.coroutine_based_application.common.coroutine.EventBus
import com.newy.algotrade.coroutine_based_application.common.event.CreateUserStrategyEvent
import com.newy.algotrade.coroutine_based_application.strategy.port.`in`.HasStrategyQuery
import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.*
import com.newy.algotrade.coroutine_based_application.user_strategy.service.UserStrategyCommandService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
open class SpringUserStrategyCommandService(
    strategyQuery: HasStrategyQuery,
    userStrategyPort: UserStrategyPort,
    marketPort: MarketPort,
    productPort: ProductPort,
    userStrategyProductPort: SaveAllUserStrategyProductPort,
    @Qualifier("createUserStrategyEventBus") eventBus: EventBus<CreateUserStrategyEvent>,
) : UserStrategyCommandService(
    hasStrategyQuery = strategyQuery,
    userStrategyPort = userStrategyPort,
    marketPort = marketPort,
    productPort = productPort,
    saveAllUserStrategyProductPort = userStrategyProductPort,
    eventBus = eventBus
)