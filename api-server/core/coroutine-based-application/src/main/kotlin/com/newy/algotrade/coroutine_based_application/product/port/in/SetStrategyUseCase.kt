package com.newy.algotrade.coroutine_based_application.product.port.`in`

import com.newy.algotrade.coroutine_based_application.product.port.`in`.model.UserStrategyKey

interface SetStrategyUseCase {
    fun setStrategy(key: UserStrategyKey)
}
