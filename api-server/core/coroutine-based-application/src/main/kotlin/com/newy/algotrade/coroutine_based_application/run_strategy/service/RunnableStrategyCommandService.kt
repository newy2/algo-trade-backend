package com.newy.algotrade.coroutine_based_application.run_strategy.service

import com.newy.algotrade.coroutine_based_application.product.port.`in`.CandlesUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.RunnableStrategyUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.StrategyPort
import com.newy.algotrade.domain.chart.strategy.Strategy
import com.newy.algotrade.domain.user_strategy.UserStrategyKey

open class RunnableStrategyCommandService(
    private val candlesUseCase: CandlesUseCase,
    private val strategyPort: StrategyPort,
) : RunnableStrategyUseCase {
    override suspend fun setRunnableStrategy(userStrategyKey: UserStrategyKey) {
        strategyPort.setStrategy(
            key = userStrategyKey,
            strategy = Strategy.fromClassName(
                strategyClassName = userStrategyKey.strategyClassName,
                candles = candlesUseCase.setCandles(userStrategyKey.productPriceKey)
            )
        )
    }

    override suspend fun removeRunnableStrategy(userStrategyKey: UserStrategyKey) {
        strategyPort.removeStrategy(userStrategyKey)

        val productPriceKey = userStrategyKey.productPriceKey
        if (!strategyPort.isUsingProductPriceKey(productPriceKey)) {
            candlesUseCase.removeCandles(productPriceKey)
        }
    }
}