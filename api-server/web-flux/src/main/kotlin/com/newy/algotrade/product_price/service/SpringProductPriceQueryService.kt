package com.newy.algotrade.product_price.service

import com.newy.algotrade.product_price.port.out.ProductPricePort
import com.newy.algotrade.product_price.port.out.SubscribablePollingProductPricePort
import org.springframework.stereotype.Service

@Service
class SpringProductPriceQueryService(
    productPricePort: ProductPricePort,
    pollingProductPricePort: SubscribablePollingProductPricePort,
) : ProductPriceQueryService(productPricePort, pollingProductPricePort)