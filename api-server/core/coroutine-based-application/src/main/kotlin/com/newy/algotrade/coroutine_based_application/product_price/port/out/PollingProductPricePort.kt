package com.newy.algotrade.coroutine_based_application.product_price.port.out

import com.newy.algotrade.domain.product_price.ProductPriceKey

interface PollingProductPricePort : SubscribablePollingProductPricePort {
    suspend fun start()
    fun cancel()
}

interface SubscribablePollingProductPricePort {
    fun unSubscribe(key: ProductPriceKey)
    fun subscribe(key: ProductPriceKey)
}