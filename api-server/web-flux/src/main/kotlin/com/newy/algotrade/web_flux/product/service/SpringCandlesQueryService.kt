package com.newy.algotrade.web_flux.product.service

import com.newy.algotrade.coroutine_based_application.product.port.out.CandleQueryPort
import com.newy.algotrade.coroutine_based_application.product.service.CandlesQueryService
import org.springframework.stereotype.Service

@Service
class SpringCandlesQueryService(
    candleQueryPort: CandleQueryPort
) : CandlesQueryService(candleQueryPort)