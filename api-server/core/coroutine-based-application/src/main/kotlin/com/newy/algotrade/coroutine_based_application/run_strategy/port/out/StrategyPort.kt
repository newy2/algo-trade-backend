package com.newy.algotrade.coroutine_based_application.run_strategy.port.out

import com.newy.algotrade.domain.chart.strategy.Strategy
import com.newy.algotrade.domain.product_price.ProductPriceKey
import com.newy.algotrade.domain.user_strategy.UserStrategyKey

interface StrategyPort :
    GetStrategyFilterByProductPriceKeyPort,
    IsStrategyUsingProductPriceKeyPort,
    SetStrategyPort,
    RemoveStrategyPort

fun interface GetStrategyFilterByProductPriceKeyPort {
    fun filterBy(productPriceKey: ProductPriceKey): Map<UserStrategyKey, Strategy>
}

fun interface IsStrategyUsingProductPriceKeyPort {
    fun isUsingProductPriceKey(productPriceKey: ProductPriceKey): Boolean
}

fun interface SetStrategyPort {
    fun setStrategy(key: UserStrategyKey, strategy: Strategy)
}

fun interface RemoveStrategyPort {
    fun removeStrategy(key: UserStrategyKey)
}