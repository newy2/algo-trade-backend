package com.newy.algotrade.web_flux.run_strategy.service

import com.newy.algotrade.coroutine_based_application.product_price.port.`in`.GetCandlesQuery
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.OnCreatedStrategySignalPort
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.StrategyPort
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.StrategySignalHistoryPort
import com.newy.algotrade.coroutine_based_application.run_strategy.service.RunStrategyCommandService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
open class SpringRunStrategyCommandService(
    getCandlesQuery: GetCandlesQuery,
    strategyPort: StrategyPort,
    strategySignalHistoryPort: StrategySignalHistoryPort,
    onCreatedStrategySignalPort: OnCreatedStrategySignalPort
) : RunStrategyCommandService(getCandlesQuery, strategyPort, strategySignalHistoryPort, onCreatedStrategySignalPort)