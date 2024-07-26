package com.newy.algotrade.web_flux.product.service

import com.newy.algotrade.coroutine_based_application.product.port.out.AddCandlePort
import com.newy.algotrade.coroutine_based_application.product.service.AddCandlesService
import org.springframework.stereotype.Service

@Service
class AddCandlesSpringService(
    candlePort: AddCandlePort,
) : AddCandlesService(candlePort)