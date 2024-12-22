package com.newy.algotrade.back_testing.adapter.`in`.internal_system

import com.newy.algotrade.common.extension.ProductPrice
import com.newy.algotrade.product_price.domain.ProductPriceKey
import com.newy.algotrade.product_price.port.`in`.AddCandlesUseCase
import com.newy.algotrade.product_price.port.out.OnReceivePollingPricePort
import com.newy.algotrade.run_strategy.port.`in`.RunStrategyUseCase

open class OnReceivePollingPriceController(
    private val addCandlesUseCase: AddCandlesUseCase,
    private val runStrategyUseCase: RunStrategyUseCase,
) : OnReceivePollingPricePort {
    override suspend fun onReceivePrice(productPriceKey: ProductPriceKey, productPriceList: List<ProductPrice>) {
        addCandlesUseCase.addCandles(productPriceKey, productPriceList)
        runStrategyUseCase.runStrategy(productPriceKey)
    }
}