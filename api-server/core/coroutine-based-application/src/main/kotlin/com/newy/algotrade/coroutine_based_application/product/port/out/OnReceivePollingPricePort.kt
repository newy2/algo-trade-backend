package com.newy.algotrade.coroutine_based_application.product.port.out

import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

interface OnReceivePollingPricePort {
    suspend fun onReceivePrice(productPriceKey: ProductPriceKey, productPriceList: List<ProductPrice>)
}