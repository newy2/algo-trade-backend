package com.newy.algotrade.web_flux.product.service

import com.newy.algotrade.coroutine_based_application.product.port.out.CandlePort
import com.newy.algotrade.coroutine_based_application.product.service.CandlesQueryService
import org.springframework.stereotype.Service

@Service
class SpringCandlesQueryService(
    candlePort: CandlePort
) : CandlesQueryService(candlePort)