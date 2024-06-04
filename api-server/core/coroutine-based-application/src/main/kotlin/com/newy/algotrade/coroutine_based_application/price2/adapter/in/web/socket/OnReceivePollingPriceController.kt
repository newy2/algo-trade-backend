package com.newy.algotrade.coroutine_based_application.price2.adapter.`in`.web.socket

import com.newy.algotrade.coroutine_based_application.price2.port.`in`.candle.AddCandlesUseCase
import com.newy.algotrade.coroutine_based_application.price2.port.`in`.strategy.RunStrategyUseCase
import com.newy.algotrade.coroutine_based_application.price2.port.out.OnReceivePollingPricePort
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

class OnReceivePollingPriceController(
    private val candleUseCase: AddCandlesUseCase,
    private val runStrategyUseCase: RunStrategyUseCase,
) : OnReceivePollingPricePort {
    override suspend fun onReceivePrice(productPriceKey: ProductPriceKey, productPriceList: List<ProductPrice>) {
        candleUseCase.addCandles(productPriceKey, productPriceList)
        runStrategyUseCase.runStrategy(productPriceKey)
    }
}