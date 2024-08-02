package com.newy.algotrade.coroutine_based_application.run_strategy.adapter.out.persistent

import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.StrategyPort
import com.newy.algotrade.domain.chart.strategy.Strategy
import com.newy.algotrade.domain.price.ProductPriceKey

open class InMemoryStrategyStore : StrategyPort {
    private val map = mutableMapOf<UserStrategyKey, Strategy>()
    override fun isUsingProductPriceKey(productPriceKey: ProductPriceKey): Boolean {
        return map.keys.find { it.productPriceKey == productPriceKey } != null
    }

    override fun setStrategy(key: UserStrategyKey, strategy: Strategy) {
        map[key] = strategy
    }

    override fun removeStrategy(key: UserStrategyKey) {
        map.remove(key)
    }

    override fun filterBy(productPriceKey: ProductPriceKey): Map<UserStrategyKey, Strategy> {
        return map
            .filter { it.key.productPriceKey == productPriceKey }
    }
}