package com.newy.algotrade.market_account.service

import com.newy.algotrade.market_account.port.out.MarketAccountPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
open class SpringMarketAccountCommandService(
    marketAccountPort: MarketAccountPort,
) : MarketAccountCommandService(marketAccountPort)