package com.newy.algotrade.web_flux.product.adapter.`in`.system

import com.newy.algotrade.coroutine_based_application.product.adapter.`in`.web.SetRunnableStrategyController
import com.newy.algotrade.coroutine_based_application.product.port.`in`.SetCandlesUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.StrategyUseCase
import com.newy.algotrade.web_flux.common.annotation.InternalSystemAdapter

@InternalSystemAdapter
class SetRunnableStrategySpringController(
    candlesUseCase: SetCandlesUseCase,
    strategyUseCase: StrategyUseCase
) : SetRunnableStrategyController(candlesUseCase, strategyUseCase)