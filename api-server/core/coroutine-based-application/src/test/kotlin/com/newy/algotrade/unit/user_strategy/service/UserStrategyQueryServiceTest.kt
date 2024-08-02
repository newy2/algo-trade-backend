package com.newy.algotrade.unit.user_strategy.service

import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.UserStrategyQueryPort
import com.newy.algotrade.coroutine_based_application.user_strategy.service.UserStrategyQueryService
import com.newy.algotrade.domain.user_strategy.UserStrategyKey
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@DisplayName("port 호출순서 확인")
class UserStrategyQueryServiceTest : NoErrorUserStrategyQueryAdapter() {
    private val service = UserStrategyQueryService(this)
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

open class NoErrorUserStrategyQueryAdapter : UserStrategyQueryPort {
    override suspend fun getAllUserStrategies(): List<UserStrategyKey> = emptyList()

    override suspend fun getUserStrategy(userStrategyId: Long): UserStrategyKey? = null
}