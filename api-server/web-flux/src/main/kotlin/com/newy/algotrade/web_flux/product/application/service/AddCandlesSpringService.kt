package com.newy.algotrade.web_flux.product.application.service

import com.newy.algotrade.coroutine_based_application.product.application.service.AddCandlesService
import com.newy.algotrade.coroutine_based_application.product.port.out.AddCandlePort
import org.springframework.stereotype.Service

@Service
class AddCandlesSpringService(
    candlePort: AddCandlePort,
) : AddCandlesService(candlePort)