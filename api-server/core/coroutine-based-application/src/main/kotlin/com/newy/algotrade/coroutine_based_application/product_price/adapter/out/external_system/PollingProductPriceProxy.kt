package com.newy.algotrade.coroutine_based_application.product_price.adapter.out.external_system

import com.newy.algotrade.coroutine_based_application.product_price.port.out.PollingProductPricePort
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.product_price.ProductPriceKey

@Suppress("INAPPLICABLE_JVM_NAME")
open class PollingProductPriceProxy(
    private val components: Map<Key, PollingProductPricePort>,
) : PollingProductPricePort {
    override suspend fun start() {
        components.forEach { (_, polling) -> polling.start() }
    }

    override fun cancel() {
        components.forEach { (_, polling) -> polling.cancel() }
    }

    override fun unSubscribe(key: ProductPriceKey) {
        components[Key.from(key)]?.unSubscribe(key)
    }

    override fun subscribe(key: ProductPriceKey) {
        components[Key.from(key)]?.subscribe(key)
    }

    data class Key(val market: Market, val productType: ProductType) {
        companion object {
            fun from(productPriceKey: ProductPriceKey): Key {
                return Key(productPriceKey.market, productPriceKey.productType)
            }
        }
    }
}