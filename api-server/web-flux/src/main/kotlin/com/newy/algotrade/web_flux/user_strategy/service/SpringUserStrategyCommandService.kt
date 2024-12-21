package com.newy.algotrade.web_flux.user_strategy.service

import com.newy.algotrade.common.coroutine.EventBus
import com.newy.algotrade.common.event.CreateUserStrategyEvent
import com.newy.algotrade.strategy.port.`in`.HasStrategyQuery
import com.newy.algotrade.user_strategy.port.out.*
import com.newy.algotrade.user_strategy.service.UserStrategyCommandService
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