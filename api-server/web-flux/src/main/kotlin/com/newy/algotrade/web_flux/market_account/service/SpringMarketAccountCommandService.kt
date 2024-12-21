package com.newy.algotrade.web_flux.market_account.service

import com.newy.algotrade.market_account.port.out.MarketAccountPort
import com.newy.algotrade.market_account.service.MarketAccountCommandService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
open class SpringMarketAccountCommandService(
    marketAccountPort: MarketAccountPort,
) : MarketAccountCommandService(marketAccountPort)