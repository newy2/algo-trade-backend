package com.newy.algotrade.back_testing.adapter.`in`.web

import com.newy.algotrade.back_testing.adapter.`in`.internal_system.OnReceivePollingPriceController
import com.newy.algotrade.back_testing.adapter.out.persistence.BackTestingDataFileStorageAdapter
import com.newy.algotrade.back_testing.adapter.out.persistence.LoadBackTestingDataAdapter
import com.newy.algotrade.back_testing.domain.BackTestingDataKey
import com.newy.algotrade.back_testing.domain.BackTestingFileManager
import com.newy.algotrade.chart.domain.strategy.StrategySignalHistory
import com.newy.algotrade.chart.domain.strategy.TrafficLight
import com.newy.algotrade.common.domain.extension.ProductPrice
import com.newy.algotrade.product_price.adapter.out.volatile_storage.InMemoryCandlesStoreAdapter
import com.newy.algotrade.product_price.domain.ProductPriceKey
import com.newy.algotrade.product_price.port.`in`.ProductPriceQuery
import com.newy.algotrade.product_price.port.out.OnReceivePollingPricePort
import com.newy.algotrade.product_price.service.CandlesCommandService
import com.newy.algotrade.product_price.service.CandlesQueryService
import com.newy.algotrade.product_price.service.ProductPriceQueryService
import com.newy.algotrade.run_strategy.adapter.out.volatile_storage.InMemoryStrategySignalHistoryStoreAdapter
import com.newy.algotrade.run_strategy.adapter.out.volatile_storage.InMemoryStrategyStoreAdapter
import com.newy.algotrade.run_strategy.domain.StrategySignalHistoryKey
import com.newy.algotrade.run_strategy.port.`in`.RunnableStrategyUseCase
import com.newy.algotrade.run_strategy.port.out.OnCreatedStrategySignalPort
import com.newy.algotrade.run_strategy.service.RunStrategyCommandService
import com.newy.algotrade.run_strategy.service.RunnableStrategyCommandService
import com.newy.algotrade.user_strategy.domain.UserStrategyKey

class RunBackTestingController {
    suspend fun runBackTesting(
        backTestingDataKey: BackTestingDataKey,
        strategyClassName: String
    ): StrategySignalHistory {
        val candleStore = InMemoryCandlesStoreAdapter()
        val strategyStore = InMemoryStrategyStoreAdapter()
        val strategySignalHistoryStore = InMemoryStrategySignalHistoryStoreAdapter()

        val backTestingUserStrategyId: Long = 1
        val resultHistory = StrategySignalHistory()
        val onCreatedStrategySignalPort = OnCreatedStrategySignalPort { _, signal ->
            val history = strategySignalHistoryStore.findHistory(
                StrategySignalHistoryKey(
                    userStrategyId = backTestingUserStrategyId,
                    productPriceKey = backTestingDataKey.productPriceKey
                )
            )

            if (resultHistory.isOpened() || TrafficLight(10).isGreen(history)) {
                resultHistory.add(signal)
            }
        }

        val onReceivePollingPriceController = createOnReceivePollingPriceController(
            candleStore,
            strategyStore,
            strategySignalHistoryStore,
            onCreatedStrategySignalPort
        )

        val backTestingDataLoader = createBackTestingDataLoader(
            backTestingDataKey,
            onReceivePollingPriceController,
        ).also {
            createRunnableStrategyUseCase(
                candleStore = candleStore,
                strategyStore = strategyStore,
                backTestingDataLoader = it
            ).setRunnableStrategy(
                UserStrategyKey(
                    userStrategyId = backTestingUserStrategyId,
                    strategyClassName = strategyClassName,
                    productPriceKey = backTestingDataKey.productPriceKey
                )
            )
        }

        backTestingDataLoader.await()

        return resultHistory
    }

    private fun createBackTestingDataLoader(
        backTestingDataKey: BackTestingDataKey,
        onReceivePollingPricePort: OnReceivePollingPricePort,
    ): LoadBackTestingDataAdapter {
        val backTestingDataPort = BackTestingDataFileStorageAdapter(BackTestingFileManager())
        return LoadBackTestingDataAdapter(
            backTestingDataKey,
            backTestingDataPort,
            onReceivePollingPricePort
        )
    }

    private fun createOnReceivePollingPriceController(
        candleStore: InMemoryCandlesStoreAdapter,
        strategyStore: InMemoryStrategyStoreAdapter,
        strategySignalHistoryStore: InMemoryStrategySignalHistoryStoreAdapter,
        onCreatedStrategySignalPort: OnCreatedStrategySignalPort
    ): OnReceivePollingPricePort {
        val candlesService = CandlesCommandService(
            productPriceQuery = NullProductPriceQuery(),
            candlesPort = candleStore
        )
        val runStrategyService = RunStrategyCommandService(
            getCandlesQuery = CandlesQueryService(candleStore),
            strategyPort = strategyStore,
            strategySignalHistoryPort = strategySignalHistoryStore,
            onCreatedStrategySignalPort = onCreatedStrategySignalPort,
        )

        return OnReceivePollingPriceController(candlesService, runStrategyService)
    }

    private fun createRunnableStrategyUseCase(
        candleStore: InMemoryCandlesStoreAdapter,
        strategyStore: InMemoryStrategyStoreAdapter,
        backTestingDataLoader: LoadBackTestingDataAdapter
    ): RunnableStrategyUseCase {
        val candleService = CandlesCommandService(
            productPriceQuery = ProductPriceQueryService(
                productPricePort = backTestingDataLoader,
                pollingProductPricePort = backTestingDataLoader,
            ),
            candlesPort = candleStore
        )

        return RunnableStrategyCommandService(
            candlesUseCase = candleService,
            strategyPort = strategyStore
        )
    }
}

private class NullProductPriceQuery : ProductPriceQuery {
    override suspend fun getInitProductPrices(productPriceKey: ProductPriceKey): List<ProductPrice> {
        TODO("Not yet implemented")
    }

    override fun requestPollingProductPrice(productPriceKey: ProductPriceKey) {
        TODO("Not yet implemented")
    }

    override fun requestUnPollingProductPrice(productPriceKey: ProductPriceKey) {
        TODO("Not yet implemented")
    }
}