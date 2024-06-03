package com.newy.algotrade.coroutine_based_application.price2.port.`in`.strategy

import com.newy.algotrade.coroutine_based_application.price2.port.`in`.strategy.model.UserStrategyKey

interface SetStrategyUseCase {
    fun setStrategy(key: UserStrategyKey)
}
