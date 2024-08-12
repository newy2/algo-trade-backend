package com.newy.algotrade.web_flux.product.service

import com.newy.algotrade.coroutine_based_application.product.port.out.ProductPricePort
import com.newy.algotrade.coroutine_based_application.product.port.out.SubscribablePollingProductPricePort
import com.newy.algotrade.coroutine_based_application.product.service.ProductPriceQueryService
import org.springframework.stereotype.Service

@Service
class SpringProductPriceQueryService(
    productPricePort: ProductPricePort,
    pollingProductPricePort: SubscribablePollingProductPricePort,
) : ProductPriceQueryService(productPricePort, pollingProductPricePort)