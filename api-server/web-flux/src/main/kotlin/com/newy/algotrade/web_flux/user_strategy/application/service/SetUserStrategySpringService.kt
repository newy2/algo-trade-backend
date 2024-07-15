package com.newy.algotrade.web_flux.user_strategy.application.service

import com.newy.algotrade.coroutine_based_application.user_strategy.application.service.SetUserStrategyService
import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.*
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
) : SetUserStrategyService(marketPort, strategyPort, productPort, userStrategyPort, userStrategyProductPort)