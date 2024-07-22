package com.newy.algotrade.web_flux.product.application.service

import com.newy.algotrade.coroutine_based_application.product.application.service.UserStrategyService
import com.newy.algotrade.coroutine_based_application.product.port.out.GetUserStrategyPort
import org.springframework.stereotype.Service

@Service
class GetAllUserStrategySpringService(
    userStrategyPort: GetUserStrategyPort,
) : UserStrategyService(userStrategyPort)