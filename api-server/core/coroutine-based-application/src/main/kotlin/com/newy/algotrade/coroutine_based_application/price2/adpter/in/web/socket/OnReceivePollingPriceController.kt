package com.newy.algotrade.coroutine_based_application.price2.adpter.`in`.web.socket

import com.newy.algotrade.coroutine_based_application.price2.port.`in`.AddCandleUseCase
import com.newy.algotrade.coroutine_based_application.price2.port.`in`.RunUserStrategyUseCase
import com.newy.algotrade.coroutine_based_application.price2.port.out.OnReceivePollingPricePort
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

class OnReceivePollingPriceController(
    private val candleUseCase: AddCandleUseCase,
    private val runUserStrategyUseCase: RunUserStrategyUseCase,
) : OnReceivePollingPricePort {
    override fun onReceivePrice(productPriceKey: ProductPriceKey, productPriceList: List<ProductPrice>) {
        candleUseCase.addCandle(productPriceKey, productPriceList)
        runUserStrategyUseCase.run(productPriceKey)
    }
}