package com.newy.algotrade.web_flux.market_account.application.service

import com.newy.algotrade.coroutine_based_application.market_account.application.port.out.HasMarketAccountPort
import com.newy.algotrade.coroutine_based_application.market_account.application.port.out.SetMarketAccountPort
import com.newy.algotrade.coroutine_based_application.market_account.application.service.SetMarketAccountService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
open class SetMarketAccountSpringService(
    hasMarketAccountPort: HasMarketAccountPort,
    setMarketAccountPort: SetMarketAccountPort
) : SetMarketAccountService(hasMarketAccountPort, setMarketAccountPort)