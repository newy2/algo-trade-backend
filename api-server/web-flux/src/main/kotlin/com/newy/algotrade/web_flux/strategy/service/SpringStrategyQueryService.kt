package com.newy.algotrade.web_flux.strategy.service

import com.newy.algotrade.coroutine_based_application.strategy.port.out.StrategyPort
import com.newy.algotrade.coroutine_based_application.strategy.service.StrategyQueryService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
open class SpringStrategyQueryService(
    strategyPort: StrategyPort
) : StrategyQueryService(strategyPort)