package com.newy.algotrade.web_flux.product.service

import com.newy.algotrade.coroutine_based_application.product.port.out.GetUserStrategyPort
import com.newy.algotrade.coroutine_based_application.product.service.UserStrategyService
import org.springframework.stereotype.Service

@Service
class GetAllUserStrategySpringService(
    userStrategyPort: GetUserStrategyPort,
) : UserStrategyService(userStrategyPort)