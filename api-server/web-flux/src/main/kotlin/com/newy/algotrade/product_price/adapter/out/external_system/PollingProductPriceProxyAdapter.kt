package com.newy.algotrade.product_price.adapter.out.external_system

import com.newy.algotrade.common.consts.Market
import com.newy.algotrade.common.consts.ProductType
import com.newy.algotrade.common.coroutine.Polling
import com.newy.algotrade.product_price.domain.ProductPriceKey
import com.newy.algotrade.product_price.port.out.PollingProductPricePort

open class PollingProductPriceProxyAdapter(
    private val components: Map<Key, Polling<ProductPriceKey>>,
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