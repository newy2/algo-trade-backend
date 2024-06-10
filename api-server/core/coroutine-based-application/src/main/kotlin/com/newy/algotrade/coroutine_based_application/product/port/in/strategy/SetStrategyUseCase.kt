package com.newy.algotrade.coroutine_based_application.product.port.`in`.strategy

import com.newy.algotrade.coroutine_based_application.product.port.`in`.strategy.model.UserStrategyKey

interface SetStrategyUseCase {
    fun setStrategy(key: UserStrategyKey)
}
