package com.newy.algotrade.web_flux.user_strategy.service

import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.UserStrategyProductQueryPort
import com.newy.algotrade.coroutine_based_application.user_strategy.service.UserStrategyProductQueryService
import org.springframework.stereotype.Service

@Service
class SpringUserStrategyProductQueryService(
    userStrategyPort: UserStrategyProductQueryPort,
) : UserStrategyProductQueryService(userStrategyPort)