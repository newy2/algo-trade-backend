package com.newy.algotrade.coroutine_based_application.common.event

import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.product.ProductPriceKey

data class ReceivePollingPriceEvent(
    val productPriceKey: ProductPriceKey,
    val productPriceList: List<ProductPrice>
)