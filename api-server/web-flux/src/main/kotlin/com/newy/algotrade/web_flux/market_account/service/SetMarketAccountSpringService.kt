package com.newy.algotrade.web_flux.market_account.service

import com.newy.algotrade.coroutine_based_application.market_account.port.out.MarketAccountPort
import com.newy.algotrade.coroutine_based_application.market_account.service.SetMarketAccountService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
open class SetMarketAccountSpringService(
    marketAccountPort: MarketAccountPort,
) : SetMarketAccountService(marketAccountPort)