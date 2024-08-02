package com.newy.algotrade.coroutine_based_application.run_strategy.port.out

import com.newy.algotrade.domain.chart.strategy.Strategy
import com.newy.algotrade.domain.product.ProductPriceKey
import com.newy.algotrade.domain.user_strategy.UserStrategyKey

interface StrategyPort : StrategyQueryPort, StrategyCommandPort

interface StrategyQueryPort {
    fun filterBy(productPriceKey: ProductPriceKey): Map<UserStrategyKey, Strategy>
    fun isUsingProductPriceKey(productPriceKey: ProductPriceKey): Boolean
}

interface StrategyCommandPort {
    fun setStrategy(key: UserStrategyKey, strategy: Strategy)
    fun removeStrategy(key: UserStrategyKey)
}