package com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`

import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.model.UserStrategyKey

interface StrategyUseCase {
    fun setStrategy(key: UserStrategyKey)
    fun removeStrategy(key: UserStrategyKey)
}
