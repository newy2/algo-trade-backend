package com.newy.algotrade.web_flux.product.service

import com.newy.algotrade.coroutine_based_application.product.port.out.CandlePort
import com.newy.algotrade.coroutine_based_application.product.port.out.GetProductPricePort
import com.newy.algotrade.coroutine_based_application.product.port.out.SubscribePollingProductPricePort
import com.newy.algotrade.coroutine_based_application.product.service.SetCandlesService
import org.springframework.stereotype.Service

@Service
class SetCandlesSpringService(
    productPricePort: GetProductPricePort,
    pollingProductPricePort: SubscribePollingProductPricePort,
    candlePort: CandlePort
) : SetCandlesService(productPricePort, pollingProductPricePort, candlePort)