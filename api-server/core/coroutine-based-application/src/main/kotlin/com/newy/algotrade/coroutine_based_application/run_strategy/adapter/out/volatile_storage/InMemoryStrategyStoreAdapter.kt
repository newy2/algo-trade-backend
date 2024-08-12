package com.newy.algotrade.coroutine_based_application.run_strategy.adapter.out.volatile_storage

import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.StrategyPort
import com.newy.algotrade.domain.chart.strategy.Strategy
import com.newy.algotrade.domain.product_price.ProductPriceKey
import com.newy.algotrade.domain.user_strategy.UserStrategyKey

open class InMemoryStrategyStoreAdapter : StrategyPort {
    private val map = mutableMapOf<UserStrategyKey, Strategy>()
    override fun isUsingProductPriceKey(productPriceKey: ProductPriceKey): Boolean {
        return map.keys.find { it.productPriceKey == productPriceKey } != null
    }

    override fun saveStrategy(key: UserStrategyKey, strategy: Strategy) {
        map[key] = strategy
    }

    override fun deleteStrategy(key: UserStrategyKey) {
        map.remove(key)
    }

    override fun filterBy(productPriceKey: ProductPriceKey): Map<UserStrategyKey, Strategy> {
        return map
            .filter { it.key.productPriceKey == productPriceKey }
    }
}