package com.newy.algotrade.coroutine_based_application.price.domain

import com.newy.algotrade.coroutine_based_application.common.coroutine.Polling
import com.newy.algotrade.coroutine_based_application.price.port.out.LoadProductPricePort
import com.newy.algotrade.coroutine_based_application.price.port.out.model.LoadProductPriceParam
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import java.time.OffsetDateTime

class ProductPriceProvider(
    private val initDataLoader: LoadProductPricePort,
    private val pollingDataLoader: Polling<ProductPriceKey, List<ProductPrice>>,
    private val initDataSize: Int = 400,
) {
    private val listeners = mutableMapOf<Key, Listener>()

    suspend fun init(vararg listeners: Pair<Key, Listener>) {
        // TODO clear listener
        putAllListeners(*listeners)
        setInitData()
    }


    suspend fun putListener(key: Key, listener: Listener) {
        val isNewlyProductPriceKey = hasNotProductPriceKey(key.productPriceKey)

        fetchInitData(key.productPriceKey, listOf(listener))
        putAllListeners(key to listener)

        if (isNewlyProductPriceKey) {
            subscribeData(key.productPriceKey)
        }
    }

    fun removeListener(key: Key) {
        listeners.remove(key)
        if (hasNotProductPriceKey(key.productPriceKey)) {
            unSubscribeData(key.productPriceKey)
        }
    }

    suspend fun updatePrice(productPriceKey: ProductPriceKey, price: ProductPrice) {
        listeners
            .filter { (key, _) -> key.productPriceKey == productPriceKey }
            .forEach { (_, listener) -> listener.onUpdatePrice(productPriceKey, price) }
    }

    private fun putAllListeners(vararg listeners: Pair<Key, Listener>) {
        listeners.forEach { (key, listener) ->
            this.listeners[key] = listener
        }
    }

    private suspend fun setInitData() {
        listenersByProductPriceKey().forEach { (productPriceKey, listeners) ->
            fetchInitData(productPriceKey, listeners)
            subscribeData(productPriceKey)
        }
    }

    private fun listenersByProductPriceKey() =
        listeners.toList().groupByTo(
            mutableMapOf(),
            { (key, _) -> key.productPriceKey },
            { (_, listener) -> listener }
        )

    private fun hasNotProductPriceKey(productPriceKey: ProductPriceKey): Boolean {
        return !listeners
            .map { (eachKey, _) -> eachKey.productPriceKey }
            .contains(productPriceKey)
    }

    private suspend fun fetchInitData(productPriceKey: ProductPriceKey, listeners: List<Listener>) {
        initDataLoader.productPrices(
            LoadProductPriceParam(
                productPriceKey,
                OffsetDateTime.now(),
                initDataSize,
            )
        ).let { productPrices ->
            listeners.forEach { it.onLoadInitData(productPrices) }
        }
    }

    private suspend fun subscribeData(productPriceKey: ProductPriceKey) {
        pollingDataLoader.subscribe(productPriceKey)
    }

    private fun unSubscribeData(productPriceKey: ProductPriceKey) {
        pollingDataLoader.unSubscribe(productPriceKey)
    }

    data class Key(
        val userStrategyId: String,
        val productPriceKey: ProductPriceKey,
    )

    interface Listener {
        suspend fun onLoadInitData(prices: List<ProductPrice>)
        suspend fun onUpdatePrice(key: ProductPriceKey, price: ProductPrice)
    }
}
