package com.newy.algotrade.common.event

import com.newy.algotrade.common.domain.extension.ProductPrice
import com.newy.algotrade.product_price.domain.ProductPriceKey

data class ReceivePollingPriceEvent(
    val productPriceKey: ProductPriceKey,
    val productPriceList: List<ProductPrice>
)