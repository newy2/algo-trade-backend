package com.newy.algotrade.web_flux.product.service

import com.newy.algotrade.coroutine_based_application.product.port.out.CandlePort
import com.newy.algotrade.coroutine_based_application.product.port.out.StrategyPort
import com.newy.algotrade.coroutine_based_application.product.service.SetStrategyService
import org.springframework.stereotype.Service

@Service
class SetStrategySpringService(
    candlePort: CandlePort,
    strategyPort: StrategyPort,
) : SetStrategyService(candlePort, strategyPort)