package com.newy.algotrade.coroutine_based_application.price2.port.out

import com.newy.algotrade.coroutine_based_application.price2.port.`in`.model.UserStrategyKey
import com.newy.algotrade.domain.chart.strategy.Strategy
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

interface UserStrategyPort {
    fun hasProductPriceKey(productPriceKey: ProductPriceKey): Boolean
    fun add(key: UserStrategyKey, strategy: Strategy)
    fun remove(key: UserStrategyKey)
    fun getStrategyList(productPriceKey: ProductPriceKey): List<Strategy>
}
