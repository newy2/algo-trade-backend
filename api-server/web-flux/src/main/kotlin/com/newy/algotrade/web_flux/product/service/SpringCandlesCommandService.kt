package com.newy.algotrade.web_flux.product.service

import com.newy.algotrade.coroutine_based_application.product.port.`in`.ProductPriceQuery
import com.newy.algotrade.coroutine_based_application.product.port.out.CandlePort
import com.newy.algotrade.coroutine_based_application.product.service.CandlesCommandService
import org.springframework.stereotype.Service

@Service
class SpringCandlesCommandService(
    productPriceQuery: ProductPriceQuery,
    candlePort: CandlePort
) : CandlesCommandService(productPriceQuery, candlePort)