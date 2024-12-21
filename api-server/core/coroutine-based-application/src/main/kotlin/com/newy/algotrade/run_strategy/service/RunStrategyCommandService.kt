package com.newy.algotrade.run_strategy.service

import com.newy.algotrade.domain.chart.order.OrderType
import com.newy.algotrade.domain.chart.strategy.StrategySignal
import com.newy.algotrade.domain.product_price.ProductPriceKey
import com.newy.algotrade.domain.run_strategy.RunStrategyResult
import com.newy.algotrade.domain.run_strategy.StrategySignalHistoryKey
import com.newy.algotrade.product_price.port.`in`.GetCandlesQuery
import com.newy.algotrade.run_strategy.port.`in`.RunStrategyUseCase
import com.newy.algotrade.run_strategy.port.out.*

open class RunStrategyCommandService(
    private val getCandlesQuery: GetCandlesQuery,
    private val filterStrategyPort: FilterStrategyPort,
    private val findStrategySignalHistoryPort: FindStrategySignalHistoryPort,
    private val saveStrategySignalHistoryPort: SaveStrategySignalHistoryPort,
    private val onCreatedStrategySignalPort: OnCreatedStrategySignalPort,
) : RunStrategyUseCase {
    constructor(
        getCandlesQuery: GetCandlesQuery,
        strategyPort: StrategyPort,
        strategySignalHistoryPort: StrategySignalHistoryPort,
        onCreatedStrategySignalPort: OnCreatedStrategySignalPort,
    ) : this(
        getCandlesQuery = getCandlesQuery,
        filterStrategyPort = strategyPort,
        findStrategySignalHistoryPort = strategySignalHistoryPort,
        saveStrategySignalHistoryPort = strategySignalHistoryPort,
        onCreatedStrategySignalPort = onCreatedStrategySignalPort,
    )

    override suspend fun runStrategy(productPriceKey: ProductPriceKey): RunStrategyResult =
        RunStrategyResult().also { result ->
            val candles = getCandlesQuery.getCandles(productPriceKey)
            if (candles.size == 0) {
                return@also
            }

            filterStrategyPort.filterBy(productPriceKey)
                .also { result.totalStrategyCount = it.size }
                .map { (userStrategyKey, strategy) ->
                    // TODO Log(userStrategyKey)
                    val userStrategyId = userStrategyKey.userStrategyId
                    val orderType = strategy.shouldOperate(
                        index = candles.lastIndex,
                        history = findStrategySignalHistoryPort.findHistory(
                            StrategySignalHistoryKey(
                                userStrategyId = userStrategyId,
                                productPriceKey = productPriceKey
                            )
                        )
                    )

                    when (orderType) {
                        OrderType.NONE -> result.noneSignalCount++.also { return@map null }
                        OrderType.SELL -> result.sellSignalCount++
                        OrderType.BUY -> result.buySignalCount++
                    }

                    return@map Pair(
                        userStrategyId,
                        StrategySignal(
                            orderType = orderType,
                            timeFrame = candles.lastCandle.time,
                            price = candles.lastCandle.price.close
                        )
                    )
                }
                .filterNotNull()
                .takeIf { it.isNotEmpty() }
                ?.forEach { (userStrategyId, signal) ->
                    // TODO bulk add history
                    saveStrategySignalHistoryPort.saveHistory(
                        StrategySignalHistoryKey(
                            userStrategyId = userStrategyId,
                            productPriceKey = productPriceKey
                        ), signal
                    )
                    onCreatedStrategySignalPort.onCreatedSignal(userStrategyId, signal)
                }
        }
}