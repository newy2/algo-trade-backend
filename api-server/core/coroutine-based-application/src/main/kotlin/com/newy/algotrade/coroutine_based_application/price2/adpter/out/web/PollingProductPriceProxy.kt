package com.newy.algotrade.coroutine_based_application.price2.adpter.out.web

import com.newy.algotrade.coroutine_based_application.common.coroutine.PollingCallback
import com.newy.algotrade.coroutine_based_application.price2.port.out.OnReceivePollingPricePort
import com.newy.algotrade.coroutine_based_application.price2.port.out.PollingProductPricePort
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

@Suppress("INAPPLICABLE_JVM_NAME")
class PollingProductPriceProxy(
    private val components: Map<Key, PollingProductPricePort>,
    onReceivePollingPricePort: OnReceivePollingPricePort,
    override var callback: PollingCallback<ProductPriceKey, List<ProductPrice>>? = {
        onReceivePollingPricePort.onReceivePrice(it.first, it.second)
    },
) : PollingProductPricePort {
    init {
        callback?.let { setCallback(it) }
    }

    override suspend fun start() {
        components.forEach { (_, polling) -> polling.start() }
    }

    override fun cancel() {
        components.forEach { (_, polling) -> polling.cancel() }
    }

    override fun unSubscribe(key: ProductPriceKey) {
        components[Key.from(key)]?.unSubscribe(key)
    }

    override suspend fun subscribe(key: ProductPriceKey) {
        components[Key.from(key)]?.subscribe(key)
    }

    @JvmName("_setCallback")
    override fun setCallback(callback: PollingCallback<ProductPriceKey, List<ProductPrice>>) {
        components.forEach { (_, polling) -> polling.setCallback(callback) }
    }

    data class Key(val market: Market, val productType: ProductType) {
        companion object {
            fun from(productPriceKey: ProductPriceKey): Key {
                return Key(productPriceKey.market, productPriceKey.productType)
            }
        }
    }
}