package com.newy.algotrade.web_flux.run_strategy.service

import com.newy.algotrade.coroutine_based_application.product.port.out.CandleQueryPort
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.OnCreatedStrategySignalPort
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.StrategyQueryPort
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.StrategySignalHistoryPort
import com.newy.algotrade.coroutine_based_application.run_strategy.service.RunStrategyService
import org.springframework.stereotype.Service

@Service
class RunStrategySpringService(
    candlePort: CandleQueryPort,
    strategyPort: StrategyQueryPort,
    strategySignalHistoryPort: StrategySignalHistoryPort,
    onCreatedStrategySignalPort: OnCreatedStrategySignalPort
) : RunStrategyService(candlePort, strategyPort, strategySignalHistoryPort, onCreatedStrategySignalPort)