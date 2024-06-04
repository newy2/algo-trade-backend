package com.newy.algotrade.coroutine_based_application.price2.adapter.`in`.system

import com.newy.algotrade.coroutine_based_application.price2.adapter.`in`.web.SetUserStrategyController
import com.newy.algotrade.coroutine_based_application.price2.port.`in`.user_strategy.GetAllUserStrategyQuery

class InitController(
    private val userStrategyController: SetUserStrategyController,
    private val userStrategyQuery: GetAllUserStrategyQuery,
) {
    suspend fun init() {
        userStrategyQuery.getAllUserStrategies().forEach {
            userStrategyController.setUserStrategy(it)
        }
    }
}