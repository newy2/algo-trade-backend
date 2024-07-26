package com.newy.algotrade.unit.product.service

import com.newy.algotrade.coroutine_based_application.product.port.`in`.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.product.port.out.UserStrategyQueryPort
import com.newy.algotrade.coroutine_based_application.product.service.UserStrategyService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

open class NoErrorUserStrategyQueryAdapter : UserStrategyQueryPort {
    override suspend fun getAllUserStrategies(): List<UserStrategyKey> = emptyList()

    override suspend fun getUserStrategy(userStrategyId: Long): UserStrategyKey? = null
}

@DisplayName("port 호출순서 확인")
class UserStrategyServiceTest : NoErrorUserStrategyQueryAdapter() {
    private val service = UserStrategyService(this)
    private lateinit var log: String

    @BeforeEach
    fun setUp() {
        log = ""
    }

    @Test
    suspend fun `getAllUserStrategies 호출 확인`() {
        service.getAllUserStrategies()

        assertEquals("getAllUserStrategies ", log)
    }

    @Test
    suspend fun `getUserStrategy 호출 확인`() {
        service.getUserStrategy(1)

        assertEquals("getUserStrategy ", log)
    }

    override suspend fun getAllUserStrategies(): List<UserStrategyKey> {
        log += "getAllUserStrategies "
        return super.getAllUserStrategies()
    }

    override suspend fun getUserStrategy(userStrategyId: Long): UserStrategyKey? {
        log += "getUserStrategy "
        return super.getUserStrategy(userStrategyId)
    }
}