package com.newy.algotrade.run_strategy.port.out

import com.newy.algotrade.domain.chart.strategy.Strategy
import com.newy.algotrade.domain.product_price.ProductPriceKey
import com.newy.algotrade.domain.user_strategy.UserStrategyKey

interface StrategyPort :
    FilterStrategyPort,
    IsStrategyUsingProductPriceKeyPort,
    SaveStrategyPort,
    DeleteStrategyPort

fun interface FilterStrategyPort {
    fun filterBy(productPriceKey: ProductPriceKey): Map<UserStrategyKey, Strategy>
}

fun interface IsStrategyUsingProductPriceKeyPort {
    fun isUsingProductPriceKey(productPriceKey: ProductPriceKey): Boolean
}

fun interface SaveStrategyPort {
    fun saveStrategy(key: UserStrategyKey, strategy: Strategy)
}

fun interface DeleteStrategyPort {
    fun deleteStrategy(key: UserStrategyKey)
}