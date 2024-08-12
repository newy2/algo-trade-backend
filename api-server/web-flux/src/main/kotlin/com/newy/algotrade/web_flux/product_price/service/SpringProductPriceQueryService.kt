package com.newy.algotrade.web_flux.product_price.service

import com.newy.algotrade.coroutine_based_application.product_price.port.out.ProductPricePort
import com.newy.algotrade.coroutine_based_application.product_price.port.out.SubscribablePollingProductPricePort
import com.newy.algotrade.coroutine_based_application.product_price.service.ProductPriceQueryService
import org.springframework.stereotype.Service

@Service
class SpringProductPriceQueryService(
    productPricePort: ProductPricePort,
    pollingProductPricePort: SubscribablePollingProductPricePort,
) : ProductPriceQueryService(productPricePort, pollingProductPricePort)