package com.newy.algotrade.run_strategy.adapter.out.volatile_storage

import com.newy.algotrade.chart.domain.strategy.Strategy
import com.newy.algotrade.product_price.domain.ProductPriceKey
import com.newy.algotrade.run_strategy.port.out.StrategyPort
import com.newy.algotrade.user_strategy.domain.UserStrategyKey

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