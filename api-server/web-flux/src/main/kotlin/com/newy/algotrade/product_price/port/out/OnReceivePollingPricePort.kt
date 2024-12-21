package com.newy.algotrade.product_price.port.out

import com.newy.algotrade.common.domain.extension.ProductPrice
import com.newy.algotrade.product_price.domain.ProductPriceKey

fun interface OnReceivePollingPricePort {
    suspend fun onReceivePrice(productPriceKey: ProductPriceKey, productPriceList: List<ProductPrice>)
}