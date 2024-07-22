package com.newy.algotrade.web_flux.product.application.service

import com.newy.algotrade.coroutine_based_application.product.application.service.SetCandlesService
import com.newy.algotrade.coroutine_based_application.product.port.out.CandlePort
import com.newy.algotrade.coroutine_based_application.product.port.out.GetProductPricePort
import com.newy.algotrade.coroutine_based_application.product.port.out.SubscribePollingProductPricePort
import org.springframework.stereotype.Service

@Service
class SetCandlesSpringService(
    productPricePort: GetProductPricePort,
    pollingProductPricePort: SubscribePollingProductPricePort,
    candlePort: CandlePort
) : SetCandlesService(productPricePort, pollingProductPricePort, candlePort)