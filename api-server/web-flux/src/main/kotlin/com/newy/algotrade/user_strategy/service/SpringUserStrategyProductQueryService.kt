package com.newy.algotrade.user_strategy.service

import com.newy.algotrade.user_strategy.port.out.UserStrategyProductPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
open class SpringUserStrategyProductQueryService(
    userStrategyProductPort: UserStrategyProductPort,
) : UserStrategyProductQueryService(userStrategyProductPort)