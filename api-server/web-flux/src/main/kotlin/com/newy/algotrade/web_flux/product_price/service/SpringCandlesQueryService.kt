package com.newy.algotrade.web_flux.product_price.service

import com.newy.algotrade.coroutine_based_application.product_price.port.out.CandlePort
import com.newy.algotrade.coroutine_based_application.product_price.service.CandlesQueryService
import org.springframework.stereotype.Service

@Service
class SpringCandlesQueryService(
    candlePort: CandlePort
) : CandlesQueryService(candlePort)