package com.newy.algotrade.web_flux.run_strategy.service

import com.newy.algotrade.coroutine_based_application.product.port.`in`.CandlesQuery
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.StrategyCommandPort
import com.newy.algotrade.coroutine_based_application.run_strategy.service.StrategyService
import org.springframework.stereotype.Service

@Service
class StrategySpringService(
    candlesQuery: CandlesQuery,
    strategyPort: StrategyCommandPort,
) : StrategyService(candlesQuery, strategyPort)