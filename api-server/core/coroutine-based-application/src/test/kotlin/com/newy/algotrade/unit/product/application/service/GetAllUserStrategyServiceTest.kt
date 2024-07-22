package com.newy.algotrade.unit.product.application.service

import com.newy.algotrade.coroutine_based_application.product.application.service.GetAllUserStrategyService
import com.newy.algotrade.coroutine_based_application.product.port.`in`.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.product.port.out.GetUserStrategyPort
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class GetAllUserStrategyServiceTest : GetUserStrategyPort {
    private var isCalled = false

    @Test
    suspend fun `port 호출 확인`() {
        val service = GetAllUserStrategyService(this)
        service.getAllUserStrategies()
        assertTrue(isCalled)
    }

    override suspend fun getAllUserStrategies(): List<UserStrategyKey> {
        isCalled = true
        return emptyList()
    }
}