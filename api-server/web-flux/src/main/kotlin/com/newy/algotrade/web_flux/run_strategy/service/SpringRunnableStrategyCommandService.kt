package com.newy.algotrade.web_flux.run_strategy.service

import com.newy.algotrade.coroutine_based_application.product.port.`in`.SetCandlesUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.StrategyUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.service.RunnableStrategyCommandService
import org.springframework.stereotype.Service

@Service
class SpringRunnableStrategyCommandService(
    candlesUseCase: SetCandlesUseCase,
    strategyUseCase: StrategyUseCase,
) : RunnableStrategyCommandService(candlesUseCase, strategyUseCase)