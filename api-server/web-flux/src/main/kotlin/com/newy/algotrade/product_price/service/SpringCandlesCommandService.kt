package com.newy.algotrade.product_price.service

import com.newy.algotrade.product_price.port.`in`.ProductPriceQuery
import com.newy.algotrade.product_price.port.out.CandlesPort
import org.springframework.stereotype.Service

@Service
class SpringCandlesCommandService(
    productPriceQuery: ProductPriceQuery,
    candlesPort: CandlesPort
) : CandlesCommandService(productPriceQuery, candlesPort)