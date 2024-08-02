package com.newy.algotrade.unit.user_strategy.service

import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.UserStrategyProductQueryPort
import com.newy.algotrade.coroutine_based_application.user_strategy.service.UserStrategyQueryService
import com.newy.algotrade.domain.user_strategy.UserStrategyKey
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@DisplayName("port 호출순서 확인")
class UserStrategyProductQueryServiceTest : NoErrorUserStrategyProductQueryAdapter() {
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
        service.getUserStrategies(1)

        assertEquals("getUserStrategy ", log)
    }

    override suspend fun getAllUserStrategies(): List<UserStrategyKey> =
        super.getAllUserStrategies().also {
            log += "getAllUserStrategies "
        }

    override suspend fun getUserStrategies(userStrategyId: Long): List<UserStrategyKey> =
        super.getUserStrategies(userStrategyId).also {
            log += "getUserStrategy "
        }
}

open class NoErrorUserStrategyProductQueryAdapter : UserStrategyProductQueryPort {
    override suspend fun getAllUserStrategies(): List<UserStrategyKey> = emptyList()
    override suspend fun getUserStrategies(userStrategyId: Long): List<UserStrategyKey> = emptyList()
}