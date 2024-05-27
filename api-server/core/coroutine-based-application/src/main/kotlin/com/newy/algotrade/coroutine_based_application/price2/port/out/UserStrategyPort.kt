package com.newy.algotrade.coroutine_based_application.price2.port.out

import com.newy.algotrade.coroutine_based_application.price2.port.`in`.model.UserStrategyKey
import com.newy.algotrade.domain.chart.strategy.Strategy
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

interface UserStrategyPort : HasUserStrategyPort, CreateUserStrategyPort, DeleteUserStrategyPort, GetUserStrategyPort

interface CreateUserStrategyPort {
    fun add(key: UserStrategyKey, strategy: Strategy)
}

interface DeleteUserStrategyPort {
    fun remove(key: UserStrategyKey)
}

interface GetUserStrategyPort {
    fun filterBy(productPriceKey: ProductPriceKey): Map<UserStrategyKey, Strategy>
}

interface HasUserStrategyPort {
    fun hasProductPriceKey(productPriceKey: ProductPriceKey): Boolean
}