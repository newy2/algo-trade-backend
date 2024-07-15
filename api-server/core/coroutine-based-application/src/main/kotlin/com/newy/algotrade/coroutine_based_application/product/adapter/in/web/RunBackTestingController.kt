package com.newy.algotrade.coroutine_based_application.product.adapter.`in`.web

import com.newy.algotrade.coroutine_based_application.product.adapter.`in`.web.socket.OnReceivePollingPriceController
import com.newy.algotrade.coroutine_based_application.product.adapter.out.persistent.*
import com.newy.algotrade.coroutine_based_application.product.application.service.AddCandlesService
import com.newy.algotrade.coroutine_based_application.product.application.service.RunStrategyService
import com.newy.algotrade.coroutine_based_application.product.application.service.SetCandlesService
import com.newy.algotrade.coroutine_based_application.product.application.service.SetStrategyService
import com.newy.algotrade.coroutine_based_application.product.domain.BackTestingFileManager
import com.newy.algotrade.coroutine_based_application.product.port.`in`.model.BackTestingDataKey
import com.newy.algotrade.coroutine_based_application.product.port.`in`.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.product.port.out.OnCreatedStrategySignalPort
import com.newy.algotrade.coroutine_based_application.product.port.out.OnReceivePollingPricePort
import com.newy.algotrade.domain.chart.strategy.StrategySignal
import com.newy.algotrade.domain.chart.strategy.StrategySignalHistory
import com.newy.algotrade.domain.chart.strategy.TrafficLight

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
        val candlesService = AddCandlesService(candleStore)
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
        val candleService = SetCandlesService(
            backTestingDataLoader,
            backTestingDataLoader,
            candleStore
        )
        val strategyService = SetStrategyService(candleStore, strategyStore)

        return SetRunnableStrategyController(candleService, strategyService)
    }
}