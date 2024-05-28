package com.newy.algotrade.coroutine_based_application.price2.adapter.`in`.web.socket

import com.newy.algotrade.coroutine_based_application.price2.port.`in`.RunStrategyUseCase
import com.newy.algotrade.coroutine_based_application.price2.port.`in`.candle.AddCandleUseCase
import com.newy.algotrade.coroutine_based_application.price2.port.out.OnReceivePollingPricePort
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

class OnReceivePollingPriceController(
    private val candleUseCase: AddCandleUseCase,
    private val runStrategyUseCase: RunStrategyUseCase,
) : OnReceivePollingPricePort {
    override fun onReceivePrice(productPriceKey: ProductPriceKey, productPriceList: List<ProductPrice>) {
        candleUseCase.addCandle(productPriceKey, productPriceList)
        runStrategyUseCase.runStrategy(productPriceKey)
    }
}