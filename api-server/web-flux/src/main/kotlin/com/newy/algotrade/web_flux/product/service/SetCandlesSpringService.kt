package com.newy.algotrade.web_flux.product.service

import com.newy.algotrade.coroutine_based_application.product.port.`in`.FetchProductPriceQuery
import com.newy.algotrade.coroutine_based_application.product.port.out.CandlePort
import com.newy.algotrade.coroutine_based_application.product.service.SetCandlesService
import org.springframework.stereotype.Service

@Service
class SetCandlesSpringService(
    fetchProductPriceQuery: FetchProductPriceQuery,
    candlePort: CandlePort
) : SetCandlesService(fetchProductPriceQuery, candlePort)