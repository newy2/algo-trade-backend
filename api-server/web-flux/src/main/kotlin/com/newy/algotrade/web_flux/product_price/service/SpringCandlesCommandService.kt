package com.newy.algotrade.web_flux.product_price.service

import com.newy.algotrade.product_price.port.`in`.ProductPriceQuery
import com.newy.algotrade.product_price.port.out.CandlesPort
import com.newy.algotrade.product_price.service.CandlesCommandService
import org.springframework.stereotype.Service

@Service
class SpringCandlesCommandService(
    productPriceQuery: ProductPriceQuery,
    candlesPort: CandlesPort
) : CandlesCommandService(productPriceQuery, candlesPort)