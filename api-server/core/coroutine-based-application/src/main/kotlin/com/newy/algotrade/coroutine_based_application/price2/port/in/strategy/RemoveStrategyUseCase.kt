package com.newy.algotrade.coroutine_based_application.price2.port.`in`.strategy

import com.newy.algotrade.coroutine_based_application.price2.port.`in`.strategy.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.price2.port.out.DeleteStrategyPort

class RemoveStrategyUseCase(
    private val strategyPort: DeleteStrategyPort
) {
    fun removeStrategy(key: UserStrategyKey) {
        strategyPort.remove(key)
    }
}