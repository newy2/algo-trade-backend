package com.newy.algotrade.coroutine_based_application.product.adapter.`in`.web.socket

import com.newy.algotrade.coroutine_based_application.product.port.`in`.AddCandlesUseCase
import com.newy.algotrade.coroutine_based_application.product.port.out.OnReceivePollingPricePort
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.RunStrategyUseCase
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

open class OnReceivePollingPriceController(
    private val candleUseCase: AddCandlesUseCase,
    private val runStrategyUseCase: RunStrategyUseCase,
) : OnReceivePollingPricePort {
    override suspend fun onReceivePrice(productPriceKey: ProductPriceKey, productPriceList: List<ProductPrice>) {
        candleUseCase.addCandles(productPriceKey, productPriceList)
        runStrategyUseCase.runStrategy(productPriceKey)
    }
}