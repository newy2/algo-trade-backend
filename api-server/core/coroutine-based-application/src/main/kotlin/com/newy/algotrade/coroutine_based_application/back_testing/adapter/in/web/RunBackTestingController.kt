package com.newy.algotrade.coroutine_based_application.back_testing.adapter.`in`.web

import com.newy.algotrade.coroutine_based_application.back_testing.adapter.out.persistent.FileBackTestingDataStore
import com.newy.algotrade.coroutine_based_application.back_testing.adapter.out.persistent.LoadBackTestingDataAdapter
import com.newy.algotrade.coroutine_based_application.back_testing.domain.BackTestingFileManager
import com.newy.algotrade.coroutine_based_application.back_testing.port.`in`.model.BackTestingDataKey
import com.newy.algotrade.coroutine_based_application.product.adapter.`in`.web.SetRunnableStrategyController
import com.newy.algotrade.coroutine_based_application.product.adapter.`in`.web.socket.OnReceivePollingPriceController
import com.newy.algotrade.coroutine_based_application.product.adapter.out.persistent.InMemoryCandleStore
import com.newy.algotrade.coroutine_based_application.product.port.`in`.FetchProductPriceQuery
import com.newy.algotrade.coroutine_based_application.product.port.out.OnReceivePollingPricePort
import com.newy.algotrade.coroutine_based_application.product.service.CandlesCommandService
import com.newy.algotrade.coroutine_based_application.product.service.FetchProductPriceService
import com.newy.algotrade.coroutine_based_application.run_strategy.adapter.out.persistent.InMemoryStrategySignalHistoryStore
import com.newy.algotrade.coroutine_based_application.run_strategy.adapter.out.persistent.InMemoryStrategyStore
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.OnCreatedStrategySignalPort
import com.newy.algotrade.coroutine_based_application.run_strategy.service.RunStrategyService
import com.newy.algotrade.coroutine_based_application.run_strategy.service.StrategyService
import com.newy.algotrade.domain.chart.strategy.StrategySignal
import com.newy.algotrade.domain.chart.strategy.StrategySignalHistory
import com.newy.algotrade.domain.chart.strategy.TrafficLight
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

class RunBackTestingController {
    suspend fun runBackTesting(
        backTestingDataKey: BackTestingDataKey,
        strategyClassName: String
    ): StrategySignalHistory {
        val candleStore = InMemoryCandleStore()
        val strategyStore = InMemoryStrategyStore()
        val strategySignalHistoryStore = InMemoryStrategySignalHistoryStore()

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
        )


        val setUserStrategyController = createSetUserStrategyController(
            candleStore,
            strategyStore,
            backTestingDataLoader
        )


        setUserStrategyController.setUserStrategy(
            UserStrategyKey(
                "backTesting",
                strategyClassName,
                backTestingDataKey.productPriceKey
            )
        )

        backTestingDataLoader.await()

        return resultHistory
    }

    private fun createBackTestingDataLoader(
        backTestingDataKey: BackTestingDataKey,
        onReceivePollingPricePort: OnReceivePollingPricePort,
    ): LoadBackTestingDataAdapter {
        val backTestingDataPort = FileBackTestingDataStore(BackTestingFileManager())
        return LoadBackTestingDataAdapter(
            backTestingDataKey,
            backTestingDataPort,
            onReceivePollingPricePort
        )
    }

    private fun createOnReceivePollingPriceController(
        candleStore: InMemoryCandleStore,
        strategyStore: InMemoryStrategyStore,
        strategySignalHistoryStore: InMemoryStrategySignalHistoryStore,
        onCreatedStrategySignalPort: OnCreatedStrategySignalPort
    ): OnReceivePollingPricePort {
        val candlesService = CandlesCommandService(
            fetchProductPriceQuery = NullFetchProductPriceQuery(),
            candlePort = candleStore
        )
        val runStrategyService = RunStrategyService(
            candleStore,
            strategyStore,
            strategySignalHistoryStore,
            onCreatedStrategySignalPort,
        )

        return OnReceivePollingPriceController(candlesService, runStrategyService)
    }

    private fun createSetUserStrategyController(
        candleStore: InMemoryCandleStore,
        strategyStore: InMemoryStrategyStore,
        backTestingDataLoader: LoadBackTestingDataAdapter
    ): SetRunnableStrategyController {
        val candleService = CandlesCommandService(
            fetchProductPriceQuery = FetchProductPriceService(
                productPricePort = backTestingDataLoader,
                pollingProductPricePort = backTestingDataLoader,
            ),
            candlePort = candleStore
        )
        val strategyService = StrategyService(candleStore, strategyStore)

        return SetRunnableStrategyController(candleService, strategyService)
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