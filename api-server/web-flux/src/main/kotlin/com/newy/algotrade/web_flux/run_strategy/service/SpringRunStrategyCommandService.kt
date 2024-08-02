package com.newy.algotrade.web_flux.run_strategy.service

import com.newy.algotrade.coroutine_based_application.product.port.`in`.CandlesQuery
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.OnCreatedStrategySignalPort
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.StrategyQueryPort
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.StrategySignalHistoryPort
import com.newy.algotrade.coroutine_based_application.run_strategy.service.RunStrategyCommandService
import org.springframework.stereotype.Service

@Service
class SpringRunStrategyCommandService(
    candlesQuery: CandlesQuery,
    strategyPort: StrategyQueryPort,
    strategySignalHistoryPort: StrategySignalHistoryPort,
    onCreatedStrategySignalPort: OnCreatedStrategySignalPort
) : RunStrategyCommandService(candlesQuery, strategyPort, strategySignalHistoryPort, onCreatedStrategySignalPort)