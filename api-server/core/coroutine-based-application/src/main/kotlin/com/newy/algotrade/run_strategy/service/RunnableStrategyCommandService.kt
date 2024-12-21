package com.newy.algotrade.run_strategy.service

import com.newy.algotrade.chart.domain.strategy.Strategy
import com.newy.algotrade.product_price.port.`in`.CandlesUseCase
import com.newy.algotrade.product_price.port.`in`.RemoveCandlesUseCase
import com.newy.algotrade.product_price.port.`in`.SetCandlesUseCase
import com.newy.algotrade.run_strategy.port.`in`.RunnableStrategyUseCase
import com.newy.algotrade.run_strategy.port.out.DeleteStrategyPort
import com.newy.algotrade.run_strategy.port.out.IsStrategyUsingProductPriceKeyPort
import com.newy.algotrade.run_strategy.port.out.SaveStrategyPort
import com.newy.algotrade.run_strategy.port.out.StrategyPort
import com.newy.algotrade.user_strategy.domain.UserStrategyKey

open class RunnableStrategyCommandService(
    private val setCandlesUseCase: SetCandlesUseCase,
    private val removeCandlesUseCase: RemoveCandlesUseCase,
    private val saveStrategyPort: SaveStrategyPort,
    private val deleteStrategyPort: DeleteStrategyPort,
    private val isStrategyUsingProductPriceKeyPort: IsStrategyUsingProductPriceKeyPort,
) : RunnableStrategyUseCase {
    constructor(candlesUseCase: CandlesUseCase, strategyPort: StrategyPort) : this(
        setCandlesUseCase = candlesUseCase,
        removeCandlesUseCase = candlesUseCase,
        saveStrategyPort = strategyPort,
        deleteStrategyPort = strategyPort,
        isStrategyUsingProductPriceKeyPort = strategyPort,
    )

    override suspend fun setRunnableStrategy(userStrategyKey: UserStrategyKey) {
        saveStrategyPort.saveStrategy(
            key = userStrategyKey,
            strategy = Strategy.fromClassName(
                strategyClassName = userStrategyKey.strategyClassName,
                candles = setCandlesUseCase.setCandles(userStrategyKey.productPriceKey)
            )
        )
    }

    override suspend fun removeRunnableStrategy(userStrategyKey: UserStrategyKey) {
        deleteStrategyPort.deleteStrategy(userStrategyKey)

        val productPriceKey = userStrategyKey.productPriceKey
        if (!isStrategyUsingProductPriceKeyPort.isUsingProductPriceKey(productPriceKey)) {
            removeCandlesUseCase.removeCandles(productPriceKey)
        }
    }
}