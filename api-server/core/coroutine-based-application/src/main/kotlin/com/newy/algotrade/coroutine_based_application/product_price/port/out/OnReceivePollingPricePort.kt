package com.newy.algotrade.coroutine_based_application.product_price.port.out

import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.product_price.ProductPriceKey

fun interface OnReceivePollingPricePort {
    suspend fun onReceivePrice(productPriceKey: ProductPriceKey, productPriceList: List<ProductPrice>)
}