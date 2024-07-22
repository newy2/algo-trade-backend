package com.newy.algotrade.unit.product.application.service

import com.newy.algotrade.coroutine_based_application.product.application.service.UserStrategyService
import com.newy.algotrade.coroutine_based_application.product.port.`in`.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.product.port.out.GetUserStrategyPort
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class UserStrategyServiceTest : GetUserStrategyPort {
    private lateinit var log: String

    @BeforeEach
    fun setUp() {
        log = ""
    }

    @Test
    suspend fun `getAllUserStrategies 호출 확인`() {
        val service = UserStrategyService(this)
        service.getAllUserStrategies()
        assertEquals("getAllUserStrategies", log)
    }

    @Test
    suspend fun `getUserStrategy 호출 확인`() {
        val service = UserStrategyService(this)
        service.getUserStrategy(1)
        assertEquals("getUserStrategy 1", log)
    }

    override suspend fun getAllUserStrategies(): List<UserStrategyKey> {
        log = "getAllUserStrategies"
        return emptyList()
    }

    override suspend fun getUserStrategy(userStrategyId: Long): UserStrategyKey? {
        log = "getUserStrategy $userStrategyId"
        return null
    }
}