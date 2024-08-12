package com.newy.algotrade.coroutine_based_application.back_testing.adapter.`in`.internal_system

import com.newy.algotrade.coroutine_based_application.product_price.port.`in`.AddCandlesUseCase
import com.newy.algotrade.coroutine_based_application.product_price.port.out.OnReceivePollingPricePort
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.RunStrategyUseCase
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.product_price.ProductPriceKey

open class OnReceivePollingPriceController(
    private val addCandlesUseCase: AddCandlesUseCase,
    private val runStrategyUseCase: RunStrategyUseCase,
) : OnReceivePollingPricePort {
    override suspend fun onReceivePrice(productPriceKey: ProductPriceKey, productPriceList: List<ProductPrice>) {
        addCandlesUseCase.addCandles(productPriceKey, productPriceList)
        runStrategyUseCase.runStrategy(productPriceKey)
    }
}