package com.newy.algotrade.coroutine_based_application.run_strategy.service

import com.newy.algotrade.coroutine_based_application.product_price.port.`in`.GetCandlesQuery
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.RunStrategyUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.*
import com.newy.algotrade.domain.chart.order.OrderType
import com.newy.algotrade.domain.chart.strategy.StrategySignal
import com.newy.algotrade.domain.product_price.ProductPriceKey
import com.newy.algotrade.domain.run_strategy.RunStrategyResult
import com.newy.algotrade.domain.run_strategy.StrategySignalHistoryKey

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

    override suspend fun runStrategy(productPriceKey: ProductPriceKey): RunStrategyResult =
        RunStrategyResult().also { result ->
            getCandlesQuery.getCandles(productPriceKey).takeIf { it.size > 0 }?.let { candles ->
                getStrategyFilterByProductPriceKeyPort.filterBy(productPriceKey)
                    .also { result.totalStrategyCount = it.size }
                    .map { (userStrategyKey, strategy) ->
                        // TODO Log(userStrategyKey)
                        val userStrategyId = userStrategyKey.userStrategyId

                        strategy.shouldOperate(
                            index = candles.lastIndex,
                            history = getStrategySignalHistoryPort.getHistory(
                                StrategySignalHistoryKey(
                                    userStrategyId = userStrategyId,
                                    productPriceKey = productPriceKey
                                )
                            )
                        ).also { orderType ->
                            when (orderType) {
                                OrderType.NONE -> result.noneSignalCount++.also { return@map null }
                                OrderType.SELL -> result.sellSignalCount++
                                OrderType.BUY -> result.buySignalCount++
                            }
                        }.let { orderType ->
                            Pair(
                                userStrategyId,
                                StrategySignal(
                                    orderType = orderType,
                                    timeFrame = candles.lastCandle.time,
                                    price = candles.lastCandle.price.close
                                )
                            )
                        }
                    }
                    .filterNotNull().takeIf { it.isNotEmpty() }?.forEach { (userStrategyId, signal) ->
                        // TODO bulk add history
                        addStrategySignalHistoryPort.addHistory(
                            StrategySignalHistoryKey(
                                userStrategyId = userStrategyId,
                                productPriceKey = productPriceKey
                            ), signal
                        )
                        onCreatedStrategySignalPort.onCreatedSignal(userStrategyId, signal)
                    }
            }
        }
}