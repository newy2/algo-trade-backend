package com.newy.algotrade.web_flux.run_strategy.service

import com.newy.algotrade.coroutine_based_application.product.port.`in`.CandlesUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.StrategyPort
import com.newy.algotrade.coroutine_based_application.run_strategy.service.RunnableStrategyCommandService
import org.springframework.stereotype.Service

@Service
class SpringRunnableStrategyCommandService(
    candlesUseCase: CandlesUseCase,
    strategyPort: StrategyPort,
) : RunnableStrategyCommandService(candlesUseCase, strategyPort)