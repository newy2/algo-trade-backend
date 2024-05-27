package com.newy.algotrade.coroutine_based_application.price2.port.out

import com.newy.algotrade.coroutine_based_application.price2.port.`in`.model.UserStrategyKey
import com.newy.algotrade.domain.chart.strategy.Strategy
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

interface StrategyPort : HasStrategyPort, CreateStrategyPort, DeleteStrategyPort, GetStrategyPort

interface CreateStrategyPort {
    fun add(key: UserStrategyKey, strategy: Strategy)
}

interface DeleteStrategyPort {
    fun remove(key: UserStrategyKey)
}

interface GetStrategyPort {
    fun filterBy(productPriceKey: ProductPriceKey): Map<UserStrategyKey, Strategy>
}

interface HasStrategyPort {
    fun hasProductPriceKey(productPriceKey: ProductPriceKey): Boolean
}