package com.newy.algotrade.web_flux.product_price.service

import com.newy.algotrade.coroutine_based_application.product_price.port.out.CandlesPort
import com.newy.algotrade.coroutine_based_application.product_price.service.CandlesQueryService
import org.springframework.stereotype.Service

@Service
class SpringCandlesQueryService(
    candlesPort: CandlesPort
) : CandlesQueryService(candlesPort)