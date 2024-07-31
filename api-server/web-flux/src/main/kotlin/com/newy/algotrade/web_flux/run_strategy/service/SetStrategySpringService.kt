package com.newy.algotrade.web_flux.run_strategy.service

import com.newy.algotrade.coroutine_based_application.product.port.out.CandlePort
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.StrategyCommandPort
import com.newy.algotrade.coroutine_based_application.run_strategy.service.SetStrategyService
import org.springframework.stereotype.Service

@Service
class SetStrategySpringService(
    candlePort: CandlePort,
    strategyPort: StrategyCommandPort,
) : SetStrategyService(candlePort, strategyPort)