package com.newy.algotrade.web_flux.product.adapter.`in`.system

import com.newy.algotrade.coroutine_based_application.product.adapter.`in`.web.SetRunnableStrategyController
import com.newy.algotrade.coroutine_based_application.product.port.`in`.SetCandlesUseCase
import com.newy.algotrade.coroutine_based_application.product.port.`in`.SetStrategyUseCase
import com.newy.algotrade.web_flux.common.annotation.InternalSystemAdapter

@InternalSystemAdapter
class SetRunnableStrategySpringController(
    candlesUseCase: SetCandlesUseCase,
    strategyUseCase: SetStrategyUseCase
) : SetRunnableStrategyController(candlesUseCase, strategyUseCase)