package com.newy.algotrade.coroutine_based_application.price2.port.`in`.strategy

import com.newy.algotrade.coroutine_based_application.price2.port.`in`.strategy.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.price2.port.out.RemoveStrategyPort

class RemoveStrategyUseCase(
    private val strategyPort: RemoveStrategyPort
) {
    fun removeStrategy(key: UserStrategyKey) {
        strategyPort.removeStrategy(key)
    }
}