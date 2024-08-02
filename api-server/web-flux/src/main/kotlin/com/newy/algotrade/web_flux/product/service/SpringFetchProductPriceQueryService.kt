package com.newy.algotrade.web_flux.product.service

import com.newy.algotrade.coroutine_based_application.product.port.out.ProductPriceQueryPort
import com.newy.algotrade.coroutine_based_application.product.port.out.SubscribablePollingProductPricePort
import com.newy.algotrade.coroutine_based_application.product.service.FetchProductPriceQueryService
import org.springframework.stereotype.Service

@Service
class SpringFetchProductPriceQueryService(
    productPricePort: ProductPriceQueryPort,
    pollingProductPricePort: SubscribablePollingProductPricePort,
) : FetchProductPriceQueryService(productPricePort, pollingProductPricePort)