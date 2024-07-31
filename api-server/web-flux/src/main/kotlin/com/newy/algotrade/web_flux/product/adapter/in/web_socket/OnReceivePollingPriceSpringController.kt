package com.newy.algotrade.web_flux.product.adapter.`in`.web_socket

import com.newy.algotrade.coroutine_based_application.product.adapter.`in`.web.socket.OnReceivePollingPriceController
import com.newy.algotrade.coroutine_based_application.product.port.`in`.AddCandlesUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.RunStrategyUseCase
import com.newy.algotrade.web_flux.common.annotation.ExternalSystemAdapter

@ExternalSystemAdapter
class OnReceivePollingPriceSpringController(
    candleUseCase: AddCandlesUseCase,
    runStrategyUseCase: RunStrategyUseCase,
) : OnReceivePollingPriceController(candleUseCase, runStrategyUseCase)