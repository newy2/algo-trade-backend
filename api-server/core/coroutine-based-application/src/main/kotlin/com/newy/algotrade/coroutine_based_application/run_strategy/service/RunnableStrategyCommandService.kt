package com.newy.algotrade.coroutine_based_application.run_strategy.service

import com.newy.algotrade.coroutine_based_application.product.port.`in`.CandlesUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.RunnableStrategyUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.StrategyPort
import com.newy.algotrade.domain.chart.strategy.Strategy

open class RunnableStrategyCommandService(
    private val candlesUseCase: CandlesUseCase,
    private val strategyPort: StrategyPort,
) : RunnableStrategyUseCase {
    override suspend fun setRunnableStrategy(userStrategyKey: UserStrategyKey) {
        val candles = candlesUseCase.setCandles(userStrategyKey.productPriceKey)
        val strategy = Strategy.fromClassName(
            strategyClassName = userStrategyKey.strategyClassName,
            candles = candles
        )

        strategyPort.setStrategy(userStrategyKey, strategy)
    }

    override suspend fun removeRunnableStrategy(userStrategyKey: UserStrategyKey) {
        strategyPort.removeStrategy(userStrategyKey)

        userStrategyKey.productPriceKey.let {
            if (!strategyPort.isUsingProductPriceKey(it)) {
                candlesUseCase.removeCandles(it)
            }
        }
    }
}