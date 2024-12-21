package com.newy.algotrade.product_price.port.out

import com.newy.algotrade.product_price.domain.ProductPriceKey

interface PollingProductPricePort : SubscribablePollingProductPricePort {
    suspend fun start()
    fun cancel()
}

interface SubscribablePollingProductPricePort {
    fun unSubscribe(key: ProductPriceKey)
    fun subscribe(key: ProductPriceKey)
}