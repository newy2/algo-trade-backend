package com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`

import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.StrategyCommandPort

class RemoveStrategyUseCase(
    private val strategyPort: StrategyCommandPort
) {
    fun removeStrategy(key: UserStrategyKey) {
        strategyPort.removeStrategy(key)
    }
}