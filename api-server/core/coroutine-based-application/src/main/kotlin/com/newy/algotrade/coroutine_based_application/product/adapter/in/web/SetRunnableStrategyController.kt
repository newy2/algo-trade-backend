package com.newy.algotrade.coroutine_based_application.product.adapter.`in`.web

import com.newy.algotrade.coroutine_based_application.product.port.`in`.SetCandlesUseCase
import com.newy.algotrade.coroutine_based_application.product.port.`in`.SetStrategyUseCase
import com.newy.algotrade.coroutine_based_application.product.port.`in`.model.UserStrategyKey

open class SetRunnableStrategyController(
    private val candlesUseCase: SetCandlesUseCase,
    private val strategyUseCase: SetStrategyUseCase,
) {
    suspend fun setUserStrategy(userStrategyKey: UserStrategyKey) {
        candlesUseCase.setCandles(userStrategyKey.productPriceKey)
        strategyUseCase.setStrategy(userStrategyKey)
    }
}