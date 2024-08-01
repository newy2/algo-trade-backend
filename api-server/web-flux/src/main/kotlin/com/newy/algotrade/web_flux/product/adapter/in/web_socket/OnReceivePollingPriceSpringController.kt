package com.newy.algotrade.web_flux.product.adapter.`in`.web_socket

import com.newy.algotrade.coroutine_based_application.product.adapter.`in`.web_socket.OnReceivePollingPriceController
import com.newy.algotrade.coroutine_based_application.product.port.`in`.AddCandlesUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.RunStrategyUseCase
import com.newy.algotrade.web_flux.common.annotation.ExternalSystemAdapter
import org.springframework.context.annotation.Lazy

@ExternalSystemAdapter
class OnReceivePollingPriceSpringController(
    @Lazy candleUseCase: AddCandlesUseCase, // TODO Refector EventBus
    runStrategyUseCase: RunStrategyUseCase,
) : OnReceivePollingPriceController(candleUseCase, runStrategyUseCase)