package com.newy.algotrade.coroutine_based_application.run_strategy.service

import com.newy.algotrade.coroutine_based_application.product.port.`in`.CandlesUseCase
import com.newy.algotrade.coroutine_based_application.product.port.`in`.RemoveCandlesUseCase
import com.newy.algotrade.coroutine_based_application.product.port.`in`.SetCandlesUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.RunnableStrategyUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.IsStrategyUsingProductPriceKeyPort
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.RemoveStrategyPort
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.SetStrategyPort
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.StrategyPort
import com.newy.algotrade.domain.chart.strategy.Strategy
import com.newy.algotrade.domain.user_strategy.UserStrategyKey

open class RunnableStrategyCommandService(
    private val setCandlesUseCase: SetCandlesUseCase,
    private val removeCandlesUseCase: RemoveCandlesUseCase,
    private val setStrategyPort: SetStrategyPort,
    private val removeStrategyPort: RemoveStrategyPort,
    private val isStrategyUsingProductPriceKeyPort: IsStrategyUsingProductPriceKeyPort,
) : RunnableStrategyUseCase {
    constructor(candlesUseCase: CandlesUseCase, strategyPort: StrategyPort) : this(
        setCandlesUseCase = candlesUseCase,
        removeCandlesUseCase = candlesUseCase,
        setStrategyPort = strategyPort,
        removeStrategyPort = strategyPort,
        isStrategyUsingProductPriceKeyPort = strategyPort,
    )

    override suspend fun setRunnableStrategy(userStrategyKey: UserStrategyKey) {
        setStrategyPort.setStrategy(
            key = userStrategyKey,
            strategy = Strategy.fromClassName(
                strategyClassName = userStrategyKey.strategyClassName,
                candles = setCandlesUseCase.setCandles(userStrategyKey.productPriceKey)
            )
        )
    }

    override suspend fun removeRunnableStrategy(userStrategyKey: UserStrategyKey) {
        removeStrategyPort.removeStrategy(userStrategyKey)

        val productPriceKey = userStrategyKey.productPriceKey
        if (!isStrategyUsingProductPriceKeyPort.isUsingProductPriceKey(productPriceKey)) {
            removeCandlesUseCase.removeCandles(productPriceKey)
        }
    }
}