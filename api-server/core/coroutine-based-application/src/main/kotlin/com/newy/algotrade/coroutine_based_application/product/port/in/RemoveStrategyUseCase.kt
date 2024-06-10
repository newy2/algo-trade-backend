package com.newy.algotrade.coroutine_based_application.product.port.`in`

import com.newy.algotrade.coroutine_based_application.product.port.`in`.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.product.port.out.RemoveStrategyPort

class RemoveStrategyUseCase(
    private val strategyPort: RemoveStrategyPort
) {
    fun removeStrategy(key: UserStrategyKey) {
        strategyPort.removeStrategy(key)
    }
}