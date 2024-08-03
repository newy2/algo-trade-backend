package com.newy.algotrade.coroutine_based_application.back_testing.adapter.`in`.web

import com.newy.algotrade.coroutine_based_application.back_testing.adapter.`in`.internal_system.OnReceivePollingPriceController
import com.newy.algotrade.coroutine_based_application.back_testing.adapter.out.persistent.BackTestingDataFileStorageAdapter
import com.newy.algotrade.coroutine_based_application.back_testing.adapter.out.persistent.LoadBackTestingDataAdapter
import com.newy.algotrade.coroutine_based_application.back_testing.domain.BackTestingFileManager
import com.newy.algotrade.coroutine_based_application.back_testing.port.`in`.model.BackTestingDataKey
import com.newy.algotrade.coroutine_based_application.product.adapter.out.volatile_storage.InMemoryCandleStoreAdapter
import com.newy.algotrade.coroutine_based_application.product.port.`in`.FetchProductPriceQuery
import com.newy.algotrade.coroutine_based_application.product.port.out.OnReceivePollingPricePort
import com.newy.algotrade.coroutine_based_application.product.service.CandlesCommandService
import com.newy.algotrade.coroutine_based_application.product.service.CandlesQueryService
import com.newy.algotrade.coroutine_based_application.product.service.FetchProductPriceQueryService
import com.newy.algotrade.coroutine_based_application.run_strategy.adapter.out.volatile_storage.InMemoryStrategySignalHistoryStoreAdapter
import com.newy.algotrade.coroutine_based_application.run_strategy.adapter.out.volatile_storage.InMemoryStrategyStoreAdapter
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.RunnableStrategyUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.OnCreatedStrategySignalPort
import com.newy.algotrade.coroutine_based_application.run_strategy.service.RunStrategyCommandService
import com.newy.algotrade.coroutine_based_application.run_strategy.service.RunnableStrategyCommandService
import com.newy.algotrade.domain.chart.strategy.StrategySignal
import com.newy.algotrade.domain.chart.strategy.StrategySignalHistory
import com.newy.algotrade.domain.chart.strategy.TrafficLight
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.product.ProductPriceKey
import com.newy.algotrade.domain.user_strategy.UserStrategyKey

class RunBackTestingController {
    suspend fun runBackTesting(
        backTestingDataKey: BackTestingDataKey,
        strategyClassName: String
    ): StrategySignalHistory {
        val candleStore = InMemoryCandleStoreAdapter()
        val strategyStore = InMemoryStrategyStoreAdapter()
        val strategySignalHistoryStore = InMemoryStrategySignalHistoryStoreAdapter()

        val resultHistory = StrategySignalHistory()
        val onCreatedStrategySignalPort = object : OnCreatedStrategySignalPort {
            override suspend fun onCreatedSignal(userStrategyId: String, signal: StrategySignal) {
                val history = strategySignalHistoryStore.getHistory("backTesting")

                if (resultHistory.isOpened() || TrafficLight(10).isGreen(history)) {
                    resultHistory.add(signal)
                }
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
                    "backTesting",
                    strategyClassName,
                    backTestingDataKey.productPriceKey
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
        candleStore: InMemoryCandleStoreAdapter,
        strategyStore: InMemoryStrategyStoreAdapter,
        strategySignalHistoryStore: InMemoryStrategySignalHistoryStoreAdapter,
        onCreatedStrategySignalPort: OnCreatedStrategySignalPort
    ): OnReceivePollingPricePort {
        val candlesService = CandlesCommandService(
            fetchProductPriceQuery = NullFetchProductPriceQuery(),
            candlePort = candleStore
        )
        val runStrategyService = RunStrategyCommandService(
            candlesQuery = CandlesQueryService(candleStore),
            strategyPort = strategyStore,
            strategySignalHistoryPort = strategySignalHistoryStore,
            onCreatedStrategySignalPort = onCreatedStrategySignalPort,
        )

        return OnReceivePollingPriceController(candlesService, runStrategyService)
    }

    private fun createRunnableStrategyUseCase(
        candleStore: InMemoryCandleStoreAdapter,
        strategyStore: InMemoryStrategyStoreAdapter,
        backTestingDataLoader: LoadBackTestingDataAdapter
    ): RunnableStrategyUseCase {
        val candleService = CandlesCommandService(
            fetchProductPriceQuery = FetchProductPriceQueryService(
                productPricePort = backTestingDataLoader,
                pollingProductPricePort = backTestingDataLoader,
            ),
            candlePort = candleStore
        )

        return RunnableStrategyCommandService(
            candlesUseCase = candleService,
            strategyPort = strategyStore
        )
    }
}

private class NullFetchProductPriceQuery : FetchProductPriceQuery {
    override suspend fun fetchInitProductPrices(productPriceKey: ProductPriceKey): List<ProductPrice> {
        TODO("Not yet implemented")
    }

    override fun requestPollingProductPrice(productPriceKey: ProductPriceKey) {
        TODO("Not yet implemented")
    }

    override fun requestUnPollingProductPrice(productPriceKey: ProductPriceKey) {
        TODO("Not yet implemented")
    }
}