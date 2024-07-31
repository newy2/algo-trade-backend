package com.newy.algotrade.coroutine_based_application.run_strategy.port.out

import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.model.UserStrategyKey
import com.newy.algotrade.domain.chart.strategy.Strategy
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

interface StrategyPort : HasStrategyPort, AddStrategyPort, RemoveStrategyPort, GetStrategyPort

interface AddStrategyPort {
    fun addStrategy(key: UserStrategyKey, strategy: Strategy)
}

interface RemoveStrategyPort {
    fun removeStrategy(key: UserStrategyKey)
}

interface GetStrategyPort {
    fun filterBy(productPriceKey: ProductPriceKey): Map<UserStrategyKey, Strategy>
}

interface HasStrategyPort {
    fun hasProductPriceKey(productPriceKey: ProductPriceKey): Boolean
}