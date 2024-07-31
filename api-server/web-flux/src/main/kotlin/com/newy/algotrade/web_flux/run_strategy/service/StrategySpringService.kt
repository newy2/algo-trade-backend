package com.newy.algotrade.web_flux.run_strategy.service

import com.newy.algotrade.coroutine_based_application.product.port.out.CandlePort
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.StrategyCommandPort
import com.newy.algotrade.coroutine_based_application.run_strategy.service.StrategyService
import org.springframework.stereotype.Service

@Service
class StrategySpringService(
    candlePort: CandlePort,
    strategyPort: StrategyCommandPort,
) : StrategyService(candlePort, strategyPort)