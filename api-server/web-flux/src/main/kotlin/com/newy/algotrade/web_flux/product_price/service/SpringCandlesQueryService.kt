package com.newy.algotrade.web_flux.product_price.service

import com.newy.algotrade.product_price.port.out.CandlesPort
import com.newy.algotrade.product_price.service.CandlesQueryService
import org.springframework.stereotype.Service

@Service
class SpringCandlesQueryService(
    candlesPort: CandlesPort
) : CandlesQueryService(candlesPort)