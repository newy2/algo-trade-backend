package com.newy.algotrade.web_flux.product.service

import com.newy.algotrade.coroutine_based_application.product.port.out.ProductPriceQueryPort
import com.newy.algotrade.coroutine_based_application.product.port.out.SubscribePollingProductPricePort
import com.newy.algotrade.coroutine_based_application.product.service.FetchProductPriceService
import org.springframework.stereotype.Service

@Service
class FetchProductPriceSpringService(
    productPricePort: ProductPriceQueryPort,
    pollingProductPricePort: SubscribePollingProductPricePort,
) : FetchProductPriceService(productPricePort, pollingProductPricePort)