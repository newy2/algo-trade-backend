package com.newy.algotrade.web_flux.product.application.service

import com.newy.algotrade.coroutine_based_application.product.application.service.SetStrategyService
import com.newy.algotrade.coroutine_based_application.product.port.out.CandlePort
import com.newy.algotrade.coroutine_based_application.product.port.out.StrategyPort
import org.springframework.stereotype.Service

@Service
class SetStrategySpringService(
    candlePort: CandlePort,
    strategyPort: StrategyPort,
) : SetStrategyService(candlePort, strategyPort)