package com.newy.algotrade.coroutine_based_application.run_strategy.service

import com.newy.algotrade.coroutine_based_application.product.port.`in`.GetCandlesQuery
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.RunStrategyUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.*
import com.newy.algotrade.domain.chart.order.OrderType
import com.newy.algotrade.domain.chart.strategy.StrategySignal
import com.newy.algotrade.domain.product.ProductPriceKey

open class RunStrategyCommandService(
    private val getCandlesQuery: GetCandlesQuery,
    private val getStrategyFilterByProductPriceKeyPort: GetStrategyFilterByProductPriceKeyPort,
    private val getStrategySignalHistoryPort: GetStrategySignalHistoryPort,
    private val addStrategySignalHistoryPort: AddStrategySignalHistoryPort,
    private val onCreatedStrategySignalPort: OnCreatedStrategySignalPort,
) : RunStrategyUseCase {
    constructor(
        getCandlesQuery: GetCandlesQuery,
        strategyPort: StrategyPort,
        strategySignalHistoryPort: StrategySignalHistoryPort,
        onCreatedStrategySignalPort: OnCreatedStrategySignalPort,
    ) : this(
        getCandlesQuery = getCandlesQuery,
        getStrategyFilterByProductPriceKeyPort = strategyPort,
        getStrategySignalHistoryPort = strategySignalHistoryPort,
        addStrategySignalHistoryPort = strategySignalHistoryPort,
        onCreatedStrategySignalPort = onCreatedStrategySignalPort,
    )

    override suspend fun runStrategy(productPriceKey: ProductPriceKey) {
        val candles = getCandlesQuery.getCandles(productPriceKey).takeIf { it.size > 0 } ?: return

        getStrategyFilterByProductPriceKeyPort.filterBy(productPriceKey).forEach { (userStrategyKey, strategy) ->
            val userStrategyId = userStrategyKey.userStrategyId

            // TODO transaction?
            val history = getStrategySignalHistoryPort.getHistory(userStrategyId)

            strategy.shouldOperate(candles.lastIndex, history).let { orderType ->
                if (orderType == OrderType.NONE) {
                    return
                }

                // TODO 확인: 이 부분이 다른 곳에서도 사용하려나?
                val signal = StrategySignal(orderType, candles.lastCandle.time, candles.lastCandle.price.close)
                addStrategySignalHistoryPort.addHistory(userStrategyId, signal)
                onCreatedStrategySignalPort.onCreatedSignal(userStrategyId, signal)
            }
        }
    }
}