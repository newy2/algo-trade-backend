package com.newy.algotrade.web_flux.user_strategy.service

import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.UserStrategyProductPort
import com.newy.algotrade.coroutine_based_application.user_strategy.service.UserStrategyProductQueryService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
open class SpringUserStrategyProductQueryService(
    userStrategyProductPort: UserStrategyProductPort,
) : UserStrategyProductQueryService(userStrategyProductPort)