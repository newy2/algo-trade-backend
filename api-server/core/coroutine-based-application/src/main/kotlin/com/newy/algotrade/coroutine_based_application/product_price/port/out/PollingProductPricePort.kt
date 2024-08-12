package com.newy.algotrade.coroutine_based_application.product_price.port.out

import com.newy.algotrade.coroutine_based_application.common.coroutine.Polling
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.product_price.ProductPriceKey

interface PollingProductPricePort :
    Polling<ProductPriceKey, List<ProductPrice>>,
    SubscribablePollingProductPricePort

interface SubscribablePollingProductPricePort {
    fun unSubscribe(key: ProductPriceKey)
    fun subscribe(key: ProductPriceKey)
}