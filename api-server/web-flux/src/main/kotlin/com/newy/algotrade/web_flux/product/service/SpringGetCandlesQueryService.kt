package com.newy.algotrade.web_flux.product.service

import com.newy.algotrade.coroutine_based_application.product.port.out.CandlePort
import com.newy.algotrade.coroutine_based_application.product.service.GetCandlesQueryService
import org.springframework.stereotype.Service

@Service
class SpringGetCandlesQueryService(
    candlePort: CandlePort
) : GetCandlesQueryService(candlePort)