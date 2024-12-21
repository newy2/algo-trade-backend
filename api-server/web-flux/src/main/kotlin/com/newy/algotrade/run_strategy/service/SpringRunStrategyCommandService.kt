package com.newy.algotrade.run_strategy.service

import com.newy.algotrade.product_price.port.`in`.GetCandlesQuery
import com.newy.algotrade.run_strategy.port.out.OnCreatedStrategySignalPort
import com.newy.algotrade.run_strategy.port.out.StrategyPort
import com.newy.algotrade.run_strategy.port.out.StrategySignalHistoryPort
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