package com.newy.algotrade.common.event

import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.product_price.ProductPriceKey

data class ReceivePollingPriceEvent(
    val productPriceKey: ProductPriceKey,
    val productPriceList: List<ProductPrice>
)