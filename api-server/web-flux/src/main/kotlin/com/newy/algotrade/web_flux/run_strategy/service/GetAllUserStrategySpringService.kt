package com.newy.algotrade.web_flux.run_strategy.service

import com.newy.algotrade.coroutine_based_application.product.port.out.UserStrategyQueryPort
import com.newy.algotrade.coroutine_based_application.product.service.UserStrategyService
import org.springframework.stereotype.Service

@Service
class GetAllUserStrategySpringService(
    userStrategyPort: UserStrategyQueryPort,
) : UserStrategyService(userStrategyPort)