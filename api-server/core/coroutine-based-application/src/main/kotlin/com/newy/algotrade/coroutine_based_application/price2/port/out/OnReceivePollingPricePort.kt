package com.newy.algotrade.coroutine_based_application.price2.port.out

import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

interface OnReceivePollingPricePort {
    fun onReceivePrice(productPriceKey: ProductPriceKey, productPriceList: List<ProductPrice>)
}